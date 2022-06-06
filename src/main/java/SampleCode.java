import agi.core.*;
import agi.core.awt.*;
import agi.stkobjects.*;
import agi.stkobjects.astrogator.*;
import cn.com.dhfabric.AUChaincode;
import cn.com.dhfabric.Communication;
import cn.com.dhfabric.Register;
import com.alibaba.fastjson.JSON;
import lzy.secret.HexStringTool;
import org.bouncycastle.crypto.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

public class SampleCode
{
    private AgStkObjectRootClass	m_AgStkObjectRootClass;
	private AgScenarioClass         m_AgScenarioClass;
    private IAgStkObjectCollection	m_ScenarioChildren;
    private IAgStkObject            m_satellite1;
    private IAgStkObject            m_satellite2;
    private IAgStkObject            m_satellite3;
    private IAgStkObject            m_sensor1;
    private IAgStkObject            m_sensor2;
    private IAgStkObject            m_sensor3;
    private IAgStkObject            m_place;
    private double                  m_sate2lat = 16.8;
    private double                  m_sate2lon = 99.3;
    private double                  m_sate3lat = 1.7;
    private double                  m_sate3lon = 101.8;


    /* package */SampleCode(AgStkObjectRootClass root)
	throws AgCoreException
	{
		this.m_AgStkObjectRootClass = root;
	}

    public void setUnits() {
        this.m_AgStkObjectRootClass.getUnitPreferences().setCurrentUnit("LongitudeUnit", "deg");
        this.m_AgStkObjectRootClass.getUnitPreferences().setCurrentUnit("LatitudeUnit", "deg");
        this.m_AgStkObjectRootClass.getUnitPreferences().setCurrentUnit("DateFormat", "UTCG");
    }

    public void loadScenario() {

	    //加载场景
	    this.m_AgStkObjectRootClass.beginUpdate();
	    this.m_AgStkObjectRootClass.loadScenario("F:\\00STKFile\\Scenario1\\Scenario1.sc");

	    //获取场景、场景子集
        this.m_AgScenarioClass = (AgScenarioClass) this.m_AgStkObjectRootClass.getCurrentScenario();
        this.m_ScenarioChildren = this.m_AgScenarioClass.getChildren();

        //场景描述
        this.m_AgScenarioClass.setShortDescription("Test secnario created via AGI Java wrapper for Object Model");
        this.m_AgScenarioClass.setLongDescription("See Short Description");

        System.out.println("==============================");
        System.out.println(" Load Scenario");
        System.out.println("==============================");

        //获取子集对象
        m_satellite1 = this.m_ScenarioChildren.getItem("Satellite_A");
        m_satellite2 = this.m_ScenarioChildren.getItem("Satellite_B");
        m_satellite3 = this.m_ScenarioChildren.getItem("Satellite_C");

        m_sensor1 = m_satellite1.getChildren().getItem("Sensor_A");
        m_sensor2 = m_satellite2.getChildren().getItem("Sensor_B");
        m_sensor3 = m_satellite3.getChildren().getItem("Sensor_C");

        m_place = this.m_ScenarioChildren.getItem("Target1");

        System.out.println("==============================");
        System.out.println(" Load Scenario Children");
        System.out.println("==============================");

        //即时数据
        IAgSatellite satellite1 = new AgSatellite(m_satellite1);
        IAgSaVO vosa1 = satellite1.getVO();
        IAgVODataDisplayCollection dataDisplay = vosa1.getDataDisplay();
        int ddcount = dataDisplay.getCount();
//        3D模块上展示LLA数据
//        for (int i = 0; i < ddcount; i++) {
//            IAgVODataDisplayElement dde = dataDisplay.getItem(i);
//            System.out.println(dde.getName());
//
//            if (dde.getName().equals("LLA Position")){
//                dde.setIsVisible(true);
//            } else if (dde.getName().equals("Velocity Heading"))
//            {
//                dde.setIsVisible(false);
//            }
//        }
        this.m_AgStkObjectRootClass.endUpdate();

    }

    public String accessComputer() {

        //开始更新场景
        this.m_AgStkObjectRootClass.beginUpdate();

        //以字符串的数据形式构造可见性分析报告
	    StringBuffer accessData = new StringBuffer();

	    //关联两个场景子对象，获取可见性分析对象
        IAgStkAccess accessToObject1 = m_sensor1.getAccessToObject(m_place);
        IAgStkAccess accessToObject2 = m_sensor2.getAccessToObject(m_place);
        IAgStkAccess accessToObject3 = m_sensor3.getAccessToObject(m_place);

        //执行可见性计算
        accessToObject1.computeAccess();
        //获取可见性计算结果
        IAgDataProviderCollection dataProviders = accessToObject1.getDataProviders();
        IAgDataPrvInterval access_data = dataProviders.getDataPrvIntervalFromPath("Access Data");
        //设置场景时间约束
        IAgDrResult result = access_data.exec("30 Apr 2022 04:00:00.000", "30 Apr 2022 06:00:00.000");
        IAgDrDataSetCollection dataSets = result.getDataSets();
        //根据数据条目，自定义获取相关数据结果，本例获取开始时间、结束时间、观测时长三项数据
        Object[] startTime = (Object[])dataSets.getItem(1).getValues_AsObject();
        Object[] stopTime = (Object[])dataSets.getItem(2).getValues_AsObject();
        Object[] duration = (Object[])dataSets.getItem(3).getValues_AsObject();

        accessData.append("\n\n卫星A可见性分析报告\n"+"开始时间:  "+startTime[0]+"\n结束时间:  "+stopTime[0]+"\n持续时间:  "+duration[0]);

        accessToObject2.computeAccess();
        dataProviders = accessToObject2.getDataProviders();
        access_data = dataProviders.getDataPrvIntervalFromPath("Access Data");
        result = access_data.exec("30 Apr 2022 04:00:00.000", "30 Apr 2022 06:00:00.000");
        dataSets = result.getDataSets();
        startTime = (Object[])dataSets.getItem(1).getValues_AsObject();
        stopTime = (Object[])dataSets.getItem(2).getValues_AsObject();
        duration = (Object[])dataSets.getItem(3).getValues_AsObject();

        accessData.append("\n\n卫星B可见性分析报告\n"+"开始时间:  "+startTime[0]+"\n结束时间:  "+stopTime[0]+"\n持续时间:  "+duration[0]);

        accessToObject3.computeAccess();
        dataProviders = accessToObject3.getDataProviders();
        access_data = dataProviders.getDataPrvIntervalFromPath("Access Data");
        result = access_data.exec("30 Apr 2022 04:00:00.000", "30 Apr 2022 06:00:00.000");
        dataSets = result.getDataSets();
        try {
            startTime = (Object[])dataSets.getItem(1).getValues_AsObject();
            stopTime = (Object[])dataSets.getItem(2).getValues_AsObject();
            duration = (Object[])dataSets.getItem(3).getValues_AsObject();

            accessData.append("\n\n卫星C可见性分析报告\n"+"开始时间:  "+startTime[0]+"\n结束时间:  "+stopTime[0]+"\n持续时间:  "+duration[0]);
        }catch (Exception e) {
            accessData.append("\n\n卫星C可见性分析报告\n未观测到!");
        }

        this.m_AgStkObjectRootClass.endUpdate();

        return accessData.toString();
    }

    public void startObervation() throws InterruptedException, NoSuchAlgorithmException, InvalidKeySpecException, CryptoException, IOException, InstantiationException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InvalidArgumentException, org.hyperledger.fabric.sdk.exception.CryptoException, ClassNotFoundException, TransactionException, ProposalException {

        this.m_AgStkObjectRootClass.executeCommand("Animate * Start Forward");

    }

    public void endObervation() throws InterruptedException, NoSuchAlgorithmException, InvalidKeySpecException, CryptoException, IOException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, org.hyperledger.fabric.sdk.exception.CryptoException, InvalidArgumentException, ClassNotFoundException, TransactionException, ProposalException {

        //        this.m_AgStkObjectRootClass.beginUpdate();
        String longitude = "";
        String latitude = "";
        //从STK获取目标地点经纬度
        IAgStkAccess accessToObject1 = m_sensor1.getAccessToObject(m_place);
        //执行可见性计算
        accessToObject1.computeAccess();
        //获取可见性计算结果
        IAgDataProviderCollection dataProviders = accessToObject1.getDataProviders();
        IAgDataPrvInterval access_data = dataProviders.getDataPrvIntervalFromPath("Access Data");
        //设置场景时间约束
        IAgDrResult result = access_data.exec("30 Apr 2022 04:00:00.000", "30 Apr 2022 06:00:00.000");
        IAgDrDataSetCollection dataSets = result.getDataSets();
        //根据数据条目，自定义获取相关数据结果，本例获取经度、维度数据
        Object[] lon = (Object[])dataSets.getItem(15).getValues_AsObject();
        Object[] lat = (Object[])dataSets.getItem(16).getValues_AsObject();
        latitude = lon[0].toString().substring(0,4);
        longitude = lat[0].toString();

        String initArgs[] = {"Target","{\"longitude\":\""+longitude+"\",\"latitude\":\"" + latitude + "\"}"};
        System.out.println(initArgs[1]);
        AUChaincode auChaincode = new AUChaincode();
        auChaincode.invoke(Register.register("org1"),initArgs);
        System.out.println("上传目标地点位置");

        Thread.sleep(1000);
        Map mapa2 = auChaincode.query(Register.register("org2"), new String[]{"{\"longitude\":\"" + m_sate2lon + "\",\"latitude\":\"" + m_sate2lat + "\"}"});
        String command2 = mapa2.get(200).toString();
        if(command2.equals("Change Sensor!")){
            changeSensor(2);
        }else {
            System.out.println("卫星B不处于目标地点附近！");
        }
        Map mapa3 = auChaincode.query(Register.register("org2"), new String[]{"{\"longitude\":\"" + m_sate3lon + "\",\"latitude\":\"" + m_sate3lat + "\"}"});
        String command3 = mapa3.get(200).toString();
        if(command3.equals("Change Sensor!")){
            changeSensor(2);
        }else if(mapa3.get(200).equals("Keep unchanged!")){
            System.out.println("卫星C不处于目标地点附近！");
        }else {
            System.out.println("命令错误");
        }


//        System.out.println(Math.abs(t_lat-m_sate2lat));
//        System.out.println(Math.abs(t_lon-m_sate2lon));
//        System.out.println(Math.abs(t_lat-m_sate3lat));
//        System.out.println(Math.abs(t_lon-m_sate3lon));

//        if (Math.abs(t_lat-m_sate2lat)<=20 && Math.abs(t_lon-m_sate2lon)<=20){
//            changeSensor(2);
//        }else {
//            System.out.println("卫星B不处于目标地点附近！");
//        }
//
//        if (Math.abs(t_lat-m_sate3lat)<=20 && Math.abs(t_lon-m_sate3lon)<=20){
//            changeSensor(3);
//        }else {
//            System.out.println("卫星C不处于目标地点附近！");
//        }

        IAgDataProviderCollection dataProviders1 = m_satellite2.getDataProviders();

    }



    public void changeSensor(int num) {

        this.m_AgStkObjectRootClass.beginUpdate();

        if(num == 2){
            //根据卫星对象，获取依赖于卫星的传感器对象
            IAgSensor sensor2 = (IAgSensor)m_satellite2.getChildren().getItem("Sensor_B");
            //获取传感器配置对象，传感器模式为圆锥辐射模式
            sensor2.setPatternType(AgESnPattern.E_SN_SIMPLE_CONIC);
            IAgSnPattern pattern = sensor2.getPattern();
            AgSnSimpleConicPattern agSnSimpleConicPattern = new AgSnSimpleConicPattern(pattern);
            //修改传感器参数，即圆锥角度更新为45度
            agSnSimpleConicPattern.setConeAngle(new Double(45.0));
        } else if(num == 3){
            //根据卫星对象，获取依赖于卫星的传感器对象
            IAgSensor sensor3 = (IAgSensor)m_satellite3.getChildren().getItem("Sensor_C");
            //获取传感器配置对象，传感器模式为圆锥辐射模式
            sensor3.setPatternType(AgESnPattern.E_SN_SIMPLE_CONIC);
            IAgSnPattern pattern = sensor3.getPattern();
            AgSnSimpleConicPattern agSnSimpleConicPattern = new AgSnSimpleConicPattern(pattern);
            //修改传感器参数，即圆锥角度更新为45度
            agSnSimpleConicPattern.setConeAngle(new Double(45.0));
        }else {
            System.out.println("参数错误！");
        }



        this.m_AgStkObjectRootClass.endUpdate();


    }

    public void changeSensor2() {

        this.m_AgStkObjectRootClass.beginUpdate();

        //根据卫星对象，获取依赖于卫星的传感器对象
        IAgSensor sensor2 = (IAgSensor)m_satellite2.getChildren().getItem("Sensor_B");
        //获取传感器配置对象，传感器模式为圆锥辐射模式
        sensor2.setPatternType(AgESnPattern.E_SN_SIMPLE_CONIC);
        IAgSnPattern pattern = sensor2.getPattern();
        AgSnSimpleConicPattern agSnSimpleConicPattern = new AgSnSimpleConicPattern(pattern);
        //修改传感器参数，即圆锥角度更新为45度
        agSnSimpleConicPattern.setConeAngle(new Double(30.0));

        this.m_AgStkObjectRootClass.endUpdate();

    }

    public String encryptCommunication() throws Exception {

        Communication com = new Communication();
        return com.communication();
    }

    public void reset() {
	    changeSensor2();
        this.m_AgStkObjectRootClass.rewind();
    }

}