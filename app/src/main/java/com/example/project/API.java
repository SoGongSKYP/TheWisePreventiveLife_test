package com.example.project;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
/**
 * version1
 */
public class API {

    /**
     * Default constructor
     */
    private SimpleDateFormat mFormat;
    private Date currentDate;
    private String currDate;

    ArrayList<LocalStatistics> localList = new ArrayList<LocalStatistics>();

    private static String getTagValue(String tag, Element eElement) {
        NodeList nlList=eElement.getElementsByTagName(tag).item(0).getChildNodes();
        Node nValue=(Node)nlList.item(0);
        if(nValue==null) return null;
        return nValue.getNodeValue();
    }

    public API() {
        this.mFormat = new SimpleDateFormat("yyyyMMdd"); // 검색할 생성일 범위를 지정하기위한 현재 날짜 받아오기 위한 형식지정
        this.currentDate = Calendar.getInstance().getTime();
        this.currDate = this.mFormat.format(this.currentDate); // 현재 날짜 저장
    }

    public void nation_API() throws IOException, ParserConfigurationException, SAXException, ParseException {
        Date staticsDate;
        Integer patientNum;
        Integer deadNum;
        Integer healerNum;
        Integer testNum;
        Integer careNum;
        Integer testNeg;
        Integer testCnt;
        Integer testCntComplete;
        double accDefRate;

        String parsingUrl="";

        StringBuilder urlBuilder = new StringBuilder("http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19InfStateJson"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=0eMMHHcbnpAK1eXmexxzB4pMr9lfDCq4Tl6P4wh2DrYWPkvQfiB0u9Vr5mMh39H6x63xk%2FesCnLgUfMbHBQV8g%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + URLEncoder.encode("-", "UTF-8")); /*공공데이터포털에서 받은 인증키*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("startCreateDt","UTF-8") + "=" + URLEncoder.encode(this.currDate, "UTF-8")); /*검색할 생성일 범위의 시작*/
        urlBuilder.append("&" + URLEncoder.encode("endCreateDt","UTF-8") + "=" + URLEncoder.encode(this.currDate, "UTF-8")); /*검색할 생성일 범위의 종료*/

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        //System.out.println(sb.toString());

        parsingUrl=url.toString();
        //System.out.println(parsingUrl);

        DocumentBuilderFactory dbFactory=DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder=dbFactory.newDocumentBuilder();
        Document doc=dBuilder.parse(parsingUrl);

        doc.getDocumentElement().normalize();
        //System.out.println("Root element : "+doc.getDocumentElement().getNodeName());

        NodeList nList=doc.getElementsByTagName("item");
        //System.out.println("파싱할 리스트 수 : "+nList.getLength());

        for(int i=0; i<nList.getLength(); i++) {
            Node nNode=nList.item(i);
            if(nNode.getNodeType()==Node.ELEMENT_NODE) {
                Element eElement=(Element) nNode;
                //string을 date로 변환
                String stateDt=getTagValue("stateDt",eElement);
                SimpleDateFormat transFormat=new SimpleDateFormat("yyyy-MM-dd");
                staticsDate=transFormat.parse(stateDt);

                patientNum=Integer.parseInt(getTagValue("decideCnt",eElement));
                deadNum=Integer.parseInt(getTagValue("deathCnt",eElement));
                healerNum=Integer.parseInt(getTagValue("clearCnt",eElement));
                testNum=Integer.parseInt(getTagValue("examCnt",eElement));
                careNum=Integer.parseInt(getTagValue("careCnt",eElement));
                testNeg=Integer.parseInt(getTagValue("resutlNegCnt",eElement));
                testCnt=Integer.parseInt(getTagValue("accExamCnt",eElement));
                testCntComplete=Integer.parseInt(getTagValue("accExamCompCnt",eElement));
                accDefRate=Double.parseDouble(getTagValue("accDefRate",eElement));

                NationStatistics nation=new NationStatistics(staticsDate,patientNum,deadNum,healerNum,
                        testNum, localList,careNum,testNeg, testCnt, testCntComplete, accDefRate);
            }
        }

        //전국통계 객체 생성

    }
    public void local_API() throws IOException, ParserConfigurationException, SAXException, ParseException {


        String parsingUrl="";

        StringBuilder urlBuilder = new StringBuilder("http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19SidoInfStateJson"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=0eMMHHcbnpAK1eXmexxzB4pMr9lfDCq4Tl6P4wh2DrYWPkvQfiB0u9Vr5mMh39H6x63xk%2FesCnLgUfMbHBQV8g%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + URLEncoder.encode("-", "UTF-8")); /*공공데이터포털에서 받은 인증키*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("startCreateDt","UTF-8") + "=" + URLEncoder.encode("20200410", "UTF-8")); /*검색할 생성일 범위의 시작*/
        urlBuilder.append("&" + URLEncoder.encode("endCreateDt","UTF-8") + "=" + URLEncoder.encode(this.currDate, "UTF-8")); /*검색할 생성일 범위의 종료*/
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        //System.out.println(sb.toString());

        parsingUrl=url.toString();
        //System.out.println(parsingUrl);

        DocumentBuilderFactory dbFactory=DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder=dbFactory.newDocumentBuilder();
        Document doc=dBuilder.parse(parsingUrl);

        doc.getDocumentElement().normalize();
        //System.out.println("Root element : "+doc.getDocumentElement().getNodeName());

        NodeList nList=doc.getElementsByTagName("item");
        //System.out.println("파싱할 리스트 수 : "+nList.getLength());

        for(int i=0; i<nList.getLength(); i++) {
            Node nNode=nList.item(i);
            if(nNode.getNodeType()==Node.ELEMENT_NODE) {
                Element eElement=(Element) nNode;
                Date staticsDate;
                Integer patientNum;
                Integer deadNum;
                Integer healerNum;
                String localName;
                Place localPosition=null;
                Integer increaseDecrease;

                String stateDt=getTagValue("stdDay",eElement);
                SimpleDateFormat transFormat=new SimpleDateFormat("yyyy-MM-dd");
                staticsDate=transFormat.parse(stateDt);

                patientNum=Integer.parseInt(getTagValue("defCnt",eElement));
                deadNum=Integer.parseInt(getTagValue("deathCnt",eElement));
                healerNum=Integer.parseInt(getTagValue("isolClearCnt",eElement));
                localName=getTagValue("gubun",eElement);

                //localPosition;
                if(localName.equals("대구")) localPosition=new Place("대구광역시청",35.871577, 128.601842);
                else if(localName.equals("서울")) localPosition=new Place("서울시청", 37.566317, 126.977949);
                else if(localName.equals("경기")) localPosition=new Place("경기도청",37.275052,127.009447);
                else if(localName.equals("검역")) localPosition=new Place("검역(해외)",0,0);
                else if(localName.equals("경북")) localPosition=new Place("경상북도청",36.576184, 128.505596);
                else if(localName.equals("인천")) localPosition=new Place("인천광역시청",37.455877, 126.705562);
                else if(localName.equals("충남")) localPosition=new Place("충청남도청",36.659001, 126.672866 );
                else if(localName.equals("부산")) localPosition=new Place("부산광역시청",35.179848, 129.074874);
                else if(localName.equals("광주")) localPosition=new Place("광주광역시청",35.160059, 126.851422);
                else if(localName.equals("대전")) localPosition=new Place("대전광역시청",36.350429, 127.384937);
                else if(localName.equals("경남")) localPosition=new Place("경상남도청",35.238273, 128.692334);
                else if(localName.equals("강원")) localPosition=new Place("강원도청",37.885368, 127.729823);
                else if(localName.equals("충북")) localPosition=new Place("충청북도청",36.635820, 127.491334);
                else if(localName.equals("전남")) localPosition=new Place("전라남도청",34.816201, 126.462913);
                else if(localName.equals("전북")) localPosition=new Place("전라북도청",35.820345, 127.108735);
                else if(localName.equals("울산")) localPosition=new Place("울산광역시청",35.539620, 129.311527);
                else if(localName.equals("세종")) localPosition=new Place("세종특별자치시청",36.480131, 127.289033);
                increaseDecrease=Integer.parseInt(getTagValue("incDec",eElement));
                //지역통계
                localList.add(new LocalStatistics(staticsDate,patientNum,deadNum,healerNum,
                        localName,localPosition,increaseDecrease));

            }
        }
    }

    public ArrayList<SelectedClinic> clinic_API() throws IOException, ParserConfigurationException, SAXException {
        String sidoNm;//시도명
        String sgguNm;//시군구명
        String yadmNm;//기관명
        String code;//구분코드
        String phonenum;//전화번호
        ArrayList<SelectedClinic> clinicsList= new ArrayList<SelectedClinic>();

        String parsingUrl="";

        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B551182/pubReliefHospService/getpubReliefHospList"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=0eMMHHcbnpAK1eXmexxzB4pMr9lfDCq4Tl6P4wh2DrYWPkvQfiB0u9Vr5mMh39H6x63xk%2FesCnLgUfMbHBQV8g%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + URLEncoder.encode("-", "UTF-8")); /*공공데이터포털에서 받은 인증키*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("spclAdmTyCd","UTF-8") + "=" + URLEncoder.encode("99", "UTF-8")); /*A0: 국민안심병원/97: 코로나검사 실시기관/99: 코로나 선별진료소 운영기관*/
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        //System.out.println(sb.toString());

        parsingUrl=url.toString();
        //System.out.println(parsingUrl);

        DocumentBuilderFactory dbFactory=DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder=dbFactory.newDocumentBuilder();
        Document doc=dBuilder.parse(parsingUrl);

        doc.getDocumentElement().normalize();
        //System.out.println("Root element : "+doc.getDocumentElement().getNodeName());

        NodeList nList=doc.getElementsByTagName("item");
        //System.out.println("파싱할 리스트 수 : "+nList.getLength());

        for(int i=0; i<nList.getLength(); i++) {
            Node nNode=nList.item(i);
            if(nNode.getNodeType()==Node.ELEMENT_NODE) {
                Element eElement=(Element) nNode;
                SelectedClinic temp = new SelectedClinic(getTagValue("yadmNm",eElement),
                        null,code=getTagValue("code",eElement),phonenum=getTagValue("telno",eElement));
                temp.setPlace(temp.calXY(temp.getName()));
                clinicsList.add(temp);
            }
        }
        return clinicsList;
    }

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, ParseException {
        API a=new API();
        a.nation_API();
    }
}
