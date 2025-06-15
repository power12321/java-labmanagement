<template>
  <div class="app-container" style="display: flex; flex-direction: row; justify-content: space-around;">
    <div id="student-chart" style="width: 800px; height: 500px;"></div>
    <div id="teacher-chart" style="width: 800px; height: 500px;"></div>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue';
import {queryTeacherByPageApi, echartTop10, teacherEchartTop10} from '@/api/review/index.js';
import * as echarts from 'echarts';

const studentData = ref([]); // 用于存储学生数据
const teacherData = ref([]); // 用于存储老师数据

onMounted(async () => {
  await fetchStudentData(); // 获取学生数据
  await fetchTeacherData(); // 获取老师数据
  renderStudentChart();
  renderTeacherChart();
});

const fetchStudentData = async () => {
  try {
    const response = await echartTop10(); // 调用接口
    // 确保返回的数据是对象
    if (typeof response.data === 'object' && response.data !== null) {
      // 将对象转换为数组
      studentData.value = Object.entries(response.data).map(([name, count]) => ({ name, count }));
    } else {
      console.error('返回的数据格式不正确:', response.data);
      studentData.value = []; // 设置为空数组以避免后续错误
    }
  } catch (error) {
    console.error('获取学生数据失败:', error);
    studentData.value = []; // 设置为空数组以避免后续错误
  }
};

const fetchTeacherData = async () => {
  try {
    const response = await teacherEchartTop10(); // 调用接口
    // 确保返回的数据是对象
    if (typeof response.data === 'object' && response.data !== null) {
      // 将对象转换为数组
      teacherData.value = Object.entries(response.data).map(([name, count]) => ({ name, count }));
    } else {
      console.error('返回的数据格式不正确:', response.data);
      teacherData.value = []; // 设置为空数组以避免后续错误
    }
  } catch (error) {
    console.error('获取老师数据失败:', error);
    teacherData.value = []; // 设置为空数组以避免后续错误
  }
};

const renderStudentChart = () => {
  const studentChart = echarts.init(document.getElementById('student-chart'));
  const option = {
    title: {
      text: '学生选课排行榜',
      left: 'center',
      textStyle: {
        color: '#333',
        fontSize: 24,
      },
    },
    tooltip: {},
    xAxis: {
      type: 'category',
      data: studentData.value.map(item => item.name), // 使用接口返回的学生名称
      axisLabel: {
        textStyle: {
          color: '#666',
        },
      },
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        textStyle: {
          color: '#666',
        },
      },
    },
    series: [{
      name: '选课人数',
      type: 'bar',
      data: studentData.value.map(item => item.count), // 使用接口返回的选课人数
      itemStyle: {
        color: '#4CAF50',
        borderColor: '#388E3C',
        borderWidth: 2,
        barBorderRadius: 5,
      },
    }],
  };
  studentChart.setOption(option);
};

const renderTeacherChart = () => {
  const teacherChart = echarts.init(document.getElementById('teacher-chart'));
  const option = {
    title: {
      text: '老师被选课比例',
      left: 'center',
      textStyle: {
        color: '#333',
        fontSize: 24,
      },
    },
    tooltip: {
      trigger: 'item',
    },
    series: [{
      name: '选课比例',
      type: 'pie',
      radius: '50%',
      data: teacherData.value.map(item => ({ value: item.count, name: item.name })), // 使用接口返回的老师数据
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)',
        },
      },
      itemStyle: {
        borderColor: '#fff',
        borderWidth: 2,
      },
    }],
  };
  teacherChart.setOption(option);
};
</script>
