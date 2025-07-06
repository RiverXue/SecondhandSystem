<template>
  <div class="publish-container">
    <el-card class="publish-card">
      <h2 class="title">发布二手商品</h2>
      <el-form
          ref="publishFormRef"
          :model="formData"
          :rules="formRules"
          class="publish-form"
          label-width="100px"
      >
        <el-form-item label="商品标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入商品标题"></el-input>
        </el-form-item>

        <el-form-item label="商品描述" prop="description">
          <el-input
              v-model="formData.description"
              :rows="4"
              placeholder="请输入商品描述"
              type="textarea"
          ></el-input>
        </el-form-item>

        <el-form-item label="商品价格" prop="price">
          <el-input
              v-model.number="formData.price"
              placeholder="请输入商品价格"
              suffix="元"
          ></el-input>
        </el-form-item>

        <el-form-item label="商品分类" prop="category">
          <el-select v-model="formData.category" placeholder="请选择商品分类">
            <el-option label="电子产品" value="electronics"></el-option>
            <el-option label="家居用品" value="home"></el-option>
            <el-option label="服装服饰" value="clothing"></el-option>
            <el-option label="图书文具" value="books"></el-option>
            <el-option label="其他" value="other"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="商品图片" prop="images">
          <!--          <el-upload-->
          <!--              ref="uploadRef"-->
          <!--              v-model:file-list="fileList"-->
          <!--              :auto-upload="false"-->
          <!--              :on-change="handleFileChange"-->
          <!--              :on-error="handleUploadError"-->
          <!--              :on-exceed="handleExceed"-->
          <!--              :on-progress="handleUploadProgress"-->
          <!--              :on-remove="handleRemove"-->
          <!--              :on-success="handleUploadSuccess"-->
          <!--              list-type="picture-card"-->
          <!--          >-->
          <el-upload
              ref="uploadRef"
              v-model:file-list="fileList"
              :auto-upload="false"
              :limit="5"
              :on-change="handleFileChange"
              :on-error="handleUploadError"
              :on-exceed="handleExceed"
              :on-progress="handleUploadProgress"
              :on-remove="handleRemove"
              :on-success="handleUploadSuccess"
              list-type="picture-card"
          >

            <template #default>
              <el-icon>
                <UploadFilled/>
              </el-icon>
              <div class="el-upload__text">点击上传</div>
            </template>
            <template #file="{ file }">
              <div class="upload-file-item">
                <img
                    :alt="file.name"
                    :onerror="() => file.url = defaultGoodsImage"
                    :src="file.url || defaultGoodsImage"
                    class="upload-image"
                />
                <div v-if="file.progress > 0 && file.progress < 100" class="upload-progress">
                  <el-progress :percentage="file.progress" :stroke-width="2"/>
                </div>
                <div class="upload-actions">
                  <el-button
                      v-if="file.error"
                      icon="RefreshCw"
                      size="small"
                      @click.stop="retryUpload(file)"
                  />
                  <el-button icon="Delete" size="small" @click.stop="handleRemove(file)"/>
                </div>
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">发布商品</el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script lang="ts" setup>
import {onMounted, reactive, ref} from 'vue';
import {useRouter} from 'vue-router';
import {ElButton, ElForm, ElIcon, ElMessage, ElUpload} from 'element-plus';
import {UploadFilled} from '@element-plus/icons-vue';
import {useUserStore} from '../store/user';
import {publishGoods} from '../api/goods';
import defaultGoodsImage from '../assets/codelogo.png';


const router = useRouter();
const userStore = useUserStore();
const publishFormRef = ref<InstanceType<typeof ElForm>>();
const uploadRef = ref<InstanceType<typeof ElUpload>>();

// 图片上传相关
const fileList = ref<File[]>([]);
const uploadUrl = (import.meta.env.VITE_APP_API_URL || 'http://localhost:7272') + '/goods/uploadImage';

// 表单数据
const formData = reactive({
  title: '',
  description: '',
  price: 0,
  category: '',
  images: [] as string[]
});

// 表单验证规则
const formRules = reactive({
  title: [
    {required: true, message: '请输入商品标题', trigger: 'blur'},
    {min: 2, max: 50, message: '标题长度在 2 到 50 个字符', trigger: 'blur'}
  ],
  description: [
    {required: true, message: '请输入商品描述', trigger: 'blur'},
    {min: 5, max: 500, message: '描述长度在 5 到 500 个字符', trigger: 'blur'}
  ],
  price: [
    {required: true, message: '请输入商品价格', trigger: 'blur'},
    {type: 'number', min: 0.01, message: '价格必须大于0', trigger: 'blur'}
  ],
  category: [
    {required: true, message: '请选择商品分类', trigger: 'change'}
  ]
});

onMounted(() => {
  // 检查用户是否登录
  if (!userStore.accessToken) {
    ElMessage.warning('请先登录');
    router.push('/login');
  }
});

// 提交表单
const handleSubmit = async () => {
  if (!publishFormRef.value) return;

  try {
    await publishFormRef.value.validate();

    // 验证图片是否上传
    // if (fileList.value.length === 0) {
    //   ElMessage.error('请至少上传一张商品图片');
    //   return;
    // }

    const submitData = new FormData();
    submitData.append('title', formData.title);
    submitData.append('description', formData.description);
    submitData.append('price', formData.price.toString());
    submitData.append('category', formData.category);

    // 添加图片文件
    if (fileList.value.length > 0) {
      // 第一张作为主图
      if (fileList.value[0].raw) {
        submitData.append('mainImage', fileList.value[0].raw as Blob);
      }
      // 其余作为多图
      for (let i = 1; i < fileList.value.length; i++) {
        if (fileList.value[i].raw) {
          submitData.append('images', fileList.value[i].raw);
        }
      }
    }

    // 调试日志
    console.log('FormData内容:');
    submitData.forEach((value, key) => {
      console.log(`${key}:`, value);
    });

    // 调用发布商品API
    const res = await publishGoods(submitData);
    if (res.data.code === 200) {
      ElMessage.success('商品发布成功');
      router.push('/');
    } else {
      ElMessage.error(res.data.message || '商品发布失败');
    }
  } catch (error: any) {
    console.error('表单验证失败:', error.message);
    ElMessage.error(error.message || '表单验证失败');
  }
};

// 取消发布
const handleCancel = () => {
  router.go(-1);
};

// 上传成功处理
const handleUploadSuccess = (response: any, file: File) => {
  if (response.code === 200) {
    // 创建图片预览URL并添加到预览列表
    const previewUrl = URL.createObjectURL(file);
    file.url = previewUrl;
    ElMessage.success('图片上传成功');
  } else {
    file.error = true;
    ElMessage.error(response.message || '图片上传失败');
  }
};

// 上传失败处理
const handleUploadError = (error: any, file: File) => {
  file.error = true;
  ElMessage.error('图片上传失败，请重试');
  console.error('图片上传错误:', error);
};

// 超出上传限制处理
const handleExceed = () => {
  ElMessage.warning('最多只能上传5张图片');
};

// 上传进度处理
const handleUploadProgress = (event: any, file: File) => {
  const percent = Math.round((event.loaded / event.total) * 100);
  file.progress = percent;
};

// 文件状态变化时更新fileList
const handleFileChange = (file: any, fileList: File[]) => {
  // 过滤掉已删除的文件
  fileList.value = fileList.filter(f => !f.status || f.status !== 'removed');
};

// 移除图片
const handleRemove = (file: any) => {
  const index = fileList.value.findIndex(item => item.name === file.name);
  if (index !== -1) {
    fileList.value.splice(index, 1);
  }
};

// 重试上传
const retryUpload = async (file: any) => {
  file.error = false;
  file.progress = 0;
  // 重新上传文件
  await uploadRef.value?.handleStart(file);
};
</script>

<style scoped>
.publish-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.publish-card {
  padding: 30px;
  background: var(--glass-bg) !important;
  backdrop-filter: var(--glass-backdrop);
  border: var(--glass-border);
  border-radius: 20px;
  box-shadow: var(--glass-shadow);
  background-image: var(--glass-highlight);
  transform: perspective(1000px) rotateY(var(--glass-distortion)) scale(var(--glass-scale));
  transition: var(--glass-transition);
  overflow: hidden;
}

.title {
  text-align: center;
  margin-bottom: 30px;
  color: var(--text-primary);
}

.publish-form {
  margin-top: 20px;
}

.upload-demo {
  margin-top: 10px;
}

.upload-file-item {
  position: relative;
}

.upload-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-progress {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  padding: 0 5px;
}

.upload-actions {
  position: absolute;
  top: 5px;
  right: 5px;
  display: flex;
  gap: 5px;
}
</style>