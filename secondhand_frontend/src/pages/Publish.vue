<template>
  <div class="publish-container">
    <el-card class="publish-card">
      <h2 class="title">发布二手商品</h2>
      <el-form
        ref="publishFormRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        class="publish-form"
      >
        <el-form-item label="商品标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入商品标题"></el-input>
        </el-form-item>

        <el-form-item label="商品描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入商品描述"
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
          <el-upload
            ref="uploadRef"
            v-model:file-list="imageUrl"
            :action="uploadUrl"
            :headers="headers"
            list-type="picture-card"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :limit="5"
            :on-exceed="handleExceed"
          >
            <el-icon><UploadFilled /></el-icon>
            <div class="el-upload__text">点击上传</div>
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

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElForm, ElUpload, ElButton, ElIcon } from 'element-plus';
import { UploadFilled } from '@element-plus/icons-vue';
import { useUserStore } from '../store/user';
import { publishGoods } from '../api/goods';

const router = useRouter();
const userStore = useUserStore();
const publishFormRef = ref<InstanceType<typeof ElForm>>();
const uploadRef = ref<InstanceType<typeof ElUpload>>();

// 图片上传相关
const imageUrl = ref<string[]>([]);
const uploadUrl = import.meta.env.VITE_APP_API_URL + '/api/upload';
const headers = ref({
  'Authorization': `Bearer ${userStore.token}`
});

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
    { required: true, message: '请输入商品标题', trigger: 'blur' },
    { min: 2, max: 50, message: '标题长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入商品描述', trigger: 'blur' },
    { min: 5, max: 500, message: '描述长度在 5 到 500 个字符', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入商品价格', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '价格必须大于0', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择商品分类', trigger: 'change' }
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

    // 调用发布商品API
    const res = await publishGoods(formData);
    if (res.data.code === 200) {
      ElMessage.success('商品发布成功');
      router.push('/');
    } else {
      ElMessage.error(res.data.message || '商品发布失败');
    }
  } catch (error) {
    console.error('表单验证失败:', error);
  }
};

// 取消发布
const handleCancel = () => {
  router.go(-1);
};

// 上传成功处理
const handleUploadSuccess = (response: any, file: any, fileList: any[]) => {
  if (response.code === 200) {
    formData.images.push(response.data.url);
    ElMessage.success('图片上传成功');
  } else {
    ElMessage.error('图片上传失败');
  }
};

// 上传失败处理
const handleUploadError = () => {
  ElMessage.error('图片上传失败，请重试');
};

// 超出上传限制处理
const handleExceed = () => {
  ElMessage.warning('最多只能上传5张图片');
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
}

.title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.publish-form {
  margin-top: 20px;
}

.upload-demo {
  margin-top: 10px;
}
</style>