package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.param.ClusterAlertPageParam;
import com.dtstack.engine.api.param.ClusterAlertParam;
import com.dtstack.engine.api.vo.alert.AlertGateTestVO;
import com.dtstack.engine.api.vo.alert.AlertGateVO;
import com.dtstack.engine.domain.AlertChannel;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.utils.FileUtil;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;

/**
 * @Auther: dazhi
 * @Date: 2021/1/25 2:40 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AlertControllerTest extends AbstractTest {

    @Autowired
    private AlertController alertController;

    private AlertChannel defaultAlterChannelComJar;
    private AlertChannel defaultAlterChannelDingDt;
    private AlertChannel defaultAlterChannelDingJar;
    private AlertChannel defaultAlterChannelMailDt;
    private AlertChannel defaultAlterChannelMailJar;
    private AlertChannel defaultAlterChannelSmsJar;

    @Before
    public void before(){
        this.defaultAlterChannelComJar = DataCollection.getData().getDefaultAlterChannelComJar();
        this.defaultAlterChannelDingDt = DataCollection.getData().getDefaultAlterChannelDingDt();
        this.defaultAlterChannelDingJar = DataCollection.getData().getDefaultAlterChannelDingJar();
        this.defaultAlterChannelMailDt = DataCollection.getData().getDefaultAlterChannelMailDt();
        this.defaultAlterChannelMailJar = DataCollection.getData().getDefaultAlterChannelMailJar();
        this.defaultAlterChannelSmsJar = DataCollection.getData().getDefaultAlterChannelSmsJar();
    }
    @Test
    public void editText() {
        AlertGateVO alertGateVO = new AlertGateVO();
        alertGateVO.setAlertGateCode("sms_jar");
        alertGateVO.setAlertGateJson("{\"className\":\"com.dtstack.sdk.example.ISmsChannelExample\"}");
        alertGateVO.setAlertGateName("测试通道");
        alertGateVO.setAlertGateSource("abc");
        alertGateVO.setIsDefault(0);

        MultipartFile file = new MockMultipartFile("ceshi", "ceshi".getBytes());

        try {
            alertController.edit(file, alertGateVO);
            alertGateVO.setId(1L);
            alertController.edit(file,alertGateVO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void setDefaultAlert() {
        ClusterAlertParam clusterAlertParam = new ClusterAlertParam();
        clusterAlertParam.setAlertId(defaultAlterChannelSmsJar.getId());
        clusterAlertParam.setAlertGateType(defaultAlterChannelSmsJar.getAlertGateType());
        alertController.setDefaultAlert(clusterAlertParam);

    }

    @Test
    public void setPageTest() {
        ClusterAlertPageParam pageParam = new ClusterAlertPageParam();
        pageParam.setCurrentPage(1);
        pageParam.setPageSize(15);
        alertController.page(pageParam);

    }

    @Test
    public void getByAlertIdTest() {
        AlertGateVO alertGateVO = new AlertGateVO();
        alertGateVO.setId(1L);

        alertController.getByAlertId(alertGateVO);
    }

    @Test
    public void getShowList() {
        alertController.listShow();
    }



    @Test
    public void getTestJar() throws Exception {
        String classPath = this.getClass().getResource("/").getPath();
        byte[] bytes = FileUtils.readFileToByteArray(new File(classPath + "/alter/console-alert-plugin-sdk-example-4.0.0.jar"));
        MultipartFile multipartFile = new MockMultipartFile("console-alert-plugin-sdk-example-4.0.0.jar","console-alert-plugin-sdk-example-4.0.0.jar","jar",bytes);

        // 测试短信jar 发送
        AlertGateTestVO alertGateTestSmsJarVO = new AlertGateTestVO();
        alertGateTestSmsJarVO.setPhones(Lists.newArrayList("13982756743"));
        testSmsJar(alertGateTestSmsJarVO,multipartFile,defaultAlterChannelSmsJar);

        // 测试邮箱jar 发送
        AlertGateTestVO alertGateTestMailJarVO = new AlertGateTestVO();
        alertGateTestMailJarVO.setEmails(Lists.newArrayList("13982756743@qq.com"));
        testSmsJar(alertGateTestMailJarVO,multipartFile,defaultAlterChannelMailJar);

        // 测试dingjar 发送
        AlertGateTestVO alertGateTestDingJarVO = new AlertGateTestVO();
        alertGateTestMailJarVO.setDings(Lists.newArrayList(""));
        testSmsJar(alertGateTestDingJarVO,multipartFile,defaultAlterChannelDingJar);

        // 测试邮箱jar 发送
        AlertGateTestVO alertGateTestComJarVO = new AlertGateTestVO();
        testSmsJar(alertGateTestComJarVO,multipartFile,defaultAlterChannelComJar);
    }

    private void testSmsJar(AlertGateTestVO alertGateTestVO,MultipartFile multipartFile,AlertChannel alertChannel) throws Exception {
        alertGateTestVO.setClusterId(-1L);
        alertGateTestVO.setAlertGateCode(alertChannel.getAlertGateCode());
        alertGateTestVO.setAlertGateJson(alertChannel.getAlertGateJson());
        alertGateTestVO.setAlertGateName("有要");
        alertGateTestVO.setAlertGateSource(alertChannel.getAlertGateSource());
        alertGateTestVO.setFilePath(alertChannel.getFilePath());
        alertGateTestVO.setIsDefault(0);

        alertController.testAlert(multipartFile,alertGateTestVO);
    }


}
