package com.xajiusuo.busi.out.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 第三方请求交互
 *
 * @author shizi
 */
@Slf4j
@Component
public class ExpAuth {
    /******************************* PKI认证报文公共部分-start ****************************/
    /**
     * 报文根结点
     */
    private static final String MSG_ROOT = "message";
    /**
     * 报文头结点
     */
    private static final String MSG_HEAD = "head";
    /**
     * 报文体结点
     */
    private static final String MSG_BODY = "body";
    /**
     * 服务版本号
     */
    private static final String MSG_VSERSION = "version";
    /**
     * 服务版本值
     */
    private static final String MSG_VSERSION_VALUE = "1.0";
    /**
     * 服务类型
     */
    private static final String MSG_SERVICE_TYPE = "serviceType";
    /**
     * 服务类型值
     */
    private static final String MSG_SERVICE_TYPE_VALUE = "AuthenService";
    /**
     * 报文体 认证方式
     */
    private static final String MSG_AUTH_MODE = "authMode";
    /**
     * 报文体 证书认证方式
     */
    private static final String MSG_AUTH_MODE_CERT_VALUE = "cert";
    /**
     * 报文体 口令认证方式
     */
    private static final String MSG_AUTH_MODE_PASSWORD_VALUE = "password";
    /**
     * 报文体 属性集
     */
    private static final String MSG_ATTRIBUTES = "attributes";
    /**
     * 报文体 属性
     */
    private static final String MSG_ATTRIBUTE = "attr";
    /**
     * 报文体 属性名
     */
    private static final String MSG_NAME = "name";
    /**
     * 报文父级节点
     */ // --hegd
    public static final String MSG_PARENT_NAME = "parentName";
    /**
     * 报文体 属性空间
     */
    private static final String MSG_NAMESPACE = "namespace";
    /**********************************PKI认证报文公共部分-end***********************************/
    /******************************* PKI认证请求报文-start ****************************/
    /**
     * 报文体 应用ID
     */
    private static final String MSG_APPID = "appId";
    /**
     * 访问控制
     */
    private static final String MSG_ACCESS_CONTROL = "accessControl";
    private static final String MSG_ACCESS_CONTROL_TRUE = "true";
    private static final String MSG_ACCESS_CONTROL_FALSE = "false";
    /**
     * 报文体 认证结点
     */
    private static final String MSG_AUTH = "authen";
    /**
     * 报文体 认证凭据
     */
    private static final String MSG_AUTHCREDENTIAL = "authCredential";
    /**
     * 报文体 客户端结点
     */
    private static final String MSG_CLIENT_INFO = "clientInfo";
    /**
     * 报文体 公钥证书
     */
    private static final String MSG_CERT_INFO = "certInfo";
    /**
     * 报文体 客户端结点
     */
    private static final String MSG_CLIENT_IP = "clientIP";
    /**
     * 报文体 detach认证请求包
     */
    private static final String MSG_DETACH = "detach";
    /**
     * 报文体 原文
     */
    private static final String MSG_ORIGINAL = "original";
    /**
     * 报文体 用户名
     */
    private static final String MSG_USERNAME = "username";
    /**
     * 报文体 口令
     */
    private static final String MSG_PASSWORD = "password";
    /**
     * 报文体 属性类型
     */
    private static final String MSG_ATTRIBUTE_TYPE = "attributeType";
    /**
     * 指定属性 portion
     */
    private static final String MSG_ATTRIBUTE_TYPE_PORTION = "portion";
    /**
     * 指定属性 all
     */
    private static final String MSG_ATTRIBUTE_TYPE_ALL = "all";
    /**********************************PKI认证请求报文-end***********************************/
    /*******************************PKI认证响应报文-start****************************/
    /**
     * 报文体 认证结果集状态
     */
    private static final String MSG_MESSAGE_STATE = "messageState";
    /**
     * 响应报文消息码
     */
    private static final String MSG_MESSAGE_CODE = "messageCode";
    /**
     * 响应报文消息描述
     */
    private static final String MSG_MESSAGE_DESC = "messageDesc";
    /**
     * 报文体 认证结果集
     */
    private static final String MSG_AUTH_RESULT_SET = "authResultSet";
    /**
     * 报文体 认证结果
     */
    private static final String MSG_AUTH_RESULT = "authResult";
    /**
     * 报文体 认证结果状态
     */
    private static final String MSG_SUCCESS = "success";
    /**
     * 报文体 认证错误码
     */
    private static final String MSG_AUTH_MESSSAGE_CODE = "authMessageCode";
    /**
     * 报文体 认证错误描述
     */
    private static final String MSG_AUTH_MESSSAGE_DESC = "authMessageDesc";
    /**********************************PKI认证响应报文-end***********************************/
    /****************************PKI认证响业务处理常量-start****************************/
    /**
     * 认证地址
     */
    private static final String KEY_AUTHURL = "authURL";
    /**
     * 应用标识
     */
    private static final String KEY_APP_ID = "appId";
    /**
     * 认证方式
     */
    private static final String KEY_CERT_AUTHEN = "certAuthen";
    /**
     * 客户端返回的认证原文，request中原文
     */
    private static final String KEY_ORIGINAL_JSP = "original_jsp";
    /**
     * 证书认证请求包
     */
    private static final String KEY_SIGNED_DATA = "signed_data";
    /**
     * 证书
     */
    private static final String KEY_CERT_CONTENT = "certInfo";
    /**
     * session中原文
     */
    public static final String KEY_ORIGINAL_DATA = "original_data";
    /****************************PKI认证响业务处理常量-end****************************/

    /**
     * PKI认证所需 应用ID、认证地址
     */
    @Value("${pkiAppId}")
    private String pkiAppId;
    @Value("${pkiAuthURL}")
    private String pkiAuthURL;

    /**
     * 通过key进行认证
     *
     * @return boolean
     */
    public boolean checkPkiAuth(boolean isSuccess, Map<String, String> attributeNodeMap) {
        // 第四步：客户端认证
        // 第五步：服务端验证认证原文
        // 第六步：应用服务端认证
        // 第七步：网关返回认证响应
        // 第八步：服务端处理
        //获取应用标识(appid)及网关认证地址(authURL)
        //errCode错误码 errDesc 错误描述
        String errCode = null;
        String errDesc = null;
        if (!StringUtils.isNotEmpty(pkiAppId) || !StringUtils.isNotEmpty(pkiAuthURL)) {
            isSuccess = false;
            log.error("应用标识或网关认证地址不可为空\n");
        }
        String original_data = attributeNodeMap.get("keyOriginalData");
        String signed_data = attributeNodeMap.get("keySignedData");
        String username = null;
        String password = null;
        if (isSuccess) {
            if (ExpAuth.isNotEmpty(original_data) && ExpAuth.isNotEmpty(signed_data)) {
                // 获取证书认证请求包
                signed_data = attributeNodeMap.get("keySignedData");
            } else {
                isSuccess = false;
                log.error("证书认证数据不完整！\n");
            }
        }
        // 第六步：应用服务端认证
        try {
            byte[] messagexml = null;
            if (isSuccess) {
                /*** 1 组装认证请求报文数据 ** 开始 **/
                Document reqDocument = DocumentHelper.createDocument();
                Element root = reqDocument.addElement(MSG_ROOT);
                Element requestHeadElement = root.addElement(MSG_HEAD);
                Element requestBodyElement = root.addElement(MSG_BODY);
                /* 组装报文头信息 */
                requestHeadElement.addElement(MSG_VSERSION).setText(
                        MSG_VSERSION_VALUE);
                requestHeadElement.addElement(MSG_SERVICE_TYPE).setText(
                        MSG_SERVICE_TYPE_VALUE);
                /* 组装报文体信息 */
                //组装客户端信息
                Element clientInfoElement = requestBodyElement.addElement(MSG_CLIENT_INFO);
                Element clientIPElement = clientInfoElement
                        .addElement(MSG_CLIENT_IP);
//                clientIPElement.setText(request.getRemoteAddr());
                // 组装应用标识信息
                requestBodyElement.addElement(MSG_APPID).setText(pkiAppId);
                Element authenElement = requestBodyElement.addElement(MSG_AUTH);
                Element authCredentialElement = authenElement
                        .addElement(MSG_AUTHCREDENTIAL);
                // 组装证书认证信息
                authCredentialElement.addAttribute(MSG_AUTH_MODE, MSG_AUTH_MODE_CERT_VALUE);
                authCredentialElement.addElement(MSG_DETACH).setText(signed_data);
                String base64_original = new BASE64Encoder().encode(original_data.getBytes());
                authCredentialElement.addElement(MSG_ORIGINAL).setText(base64_original);
                //支持X509证书  认证方式
                //获取到的证书
                // javax.security.cert.X509Certificate x509Certificate = null;
                //certInfo 为base64编码证书
                //可以使用  "certInfo =new BASE64Encoder().encode(x509Certificate.getEncoded());" 进行编码
                // authCredentialElement.addElement(MSG_CERT_INFO).setText(certInfo);
                requestBodyElement.addElement(MSG_ACCESS_CONTROL).setText(
                        MSG_ACCESS_CONTROL_TRUE);
                // 组装口令认证信息
                //username = request.getParameter( "" );//获取认证页面传递过来的用户名/口令
                //password = request.getParameter( "" );
                //authCredentialElement.addAttribute(MSG_AUTH_MODE,MSG_AUTH_MODE_PASSWORD_VALUE );
                //authCredentialElement.addElement( MSG_USERNAME ).setText(username);
                //authCredentialElement.addElement( MSG_PASSWORD ).setText(password);
                // 组装属性查询列表信息
                Element attributesElement = requestBodyElement
                        .addElement(MSG_ATTRIBUTES);
                attributesElement.addAttribute(MSG_ATTRIBUTE_TYPE,
                        MSG_ATTRIBUTE_TYPE_ALL);
                // TODO 取公共信息
				/*addAttribute(attributesElement, "X509Certificate.SubjectDN",
						"http://www.jit.com.cn/cinas/ias/ns/saml/saml11/X.509");
				addAttribute(attributesElement, "UMS.UserID",
				"http://www.jit.com.cn/ums/ns/user");
				addAttribute(attributesElement, "机构字典",
						"http://www.jit.com.cn/ums/ns/user");*/
                /*** 1 组装认证请求报文数据 ** 完毕 **/
                StringBuffer reqMessageData = new StringBuffer();
                try {
                    /*** 2 将认证请求报文写入输出流 ** 开始 **/
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    XMLWriter writer = new XMLWriter(outStream);
                    writer.write(reqDocument);
                    messagexml = outStream.toByteArray();
                    /*** 2 将认证请求报文写入输出流 ** 完毕 **/
                    reqMessageData.append("请求内容开始！\n");
                    reqMessageData.append(outStream.toString() + "\n");
                    reqMessageData.append("请求内容结束！\n");
                } catch (Exception e) {
                    isSuccess = false;
                    errDesc = e.getMessage();
                    log.error("组装请求时出现异常,失败原因：" + errDesc + "\n");
                }
            }
            /****************************************************************
             * 创建与网关的HTTP连接，发送认证请求报文，并接收认证响应报文*
             ****************************************************************/
            /*** 1 创建与网关的HTTP连接 ** 开始 **/
            int statusCode = 500;
            HttpClient httpClient = null;
            PostMethod postMethod = null;
            if (isSuccess) {
                // HTTPClient对象
                httpClient = new HttpClient();
                postMethod = new PostMethod(pkiAuthURL);
                // 设置报文传送的编码格式
                postMethod.setRequestHeader("Content-Type",
                        "text/xml;charset=UTF-8");
                /*** 2 设置发送认证请求内容 ** 开始 **/
                postMethod.setRequestBody(new ByteArrayInputStream(messagexml));
                /*** 2 设置发送认证请求内容 ** 结束 **/
                // 执行postMethod
                try {
                    /*** 3 发送通讯报文与网关通讯 ** 开始 **/
                    log.info("PKI认证pkiAuthURL----》" + pkiAuthURL + "\n");
                    statusCode = httpClient.executeMethod(postMethod);
                    log.info("网关请求结果： " + statusCode + "\n");
                    /*** 3 发送通讯报文与网关通讯 ** 结束 **/
                } catch (Exception e) {
                    isSuccess = false;
                    errCode = String.valueOf(statusCode);
                    errDesc = e.getMessage();
                    log.error("与网关连接出现异常!状态码:" + errCode + "失败原因：" + errDesc + "\n");
                    e.printStackTrace();
                }
            }
            /****************************************************************
             * 	第七步：网关返回认证响应*
             ****************************************************************/
            StringBuffer respMessageData = new StringBuffer();
            String respMessageXml = null;
            if (isSuccess) {
                // 当返回200或500状态时处理业务逻辑
                if (statusCode == HttpStatus.SC_OK
                        || statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                    // 从头中取出转向的地址
                    try {
                        /*** 4 接收通讯报文并处理 ** 开始 **/
                        byte[] inputstr = postMethod.getResponseBody();

                        ByteArrayInputStream ByteinputStream = new ByteArrayInputStream(
                                inputstr);
                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                        int ch = 0;
                        try {
                            while ((ch = ByteinputStream.read()) != -1) {
                                int upperCh = (char) ch;
                                outStream.write(upperCh);
                            }
                        } catch (Exception e) {
                            isSuccess = false;
                            errDesc = e.getMessage();
                            log.info("接收报文异常!失败原因：" + errDesc + "\n");
                        }
                        if (isSuccess) {
                            // 200 表示返回处理成功
                            if (statusCode == HttpStatus.SC_OK) {
                                respMessageData.append("响应内容开始！\n");
                                respMessageData.append(new String(outStream
                                        .toByteArray(), "UTF-8")
                                        + "\n");
                                respMessageData.append("响应内容开始！\n");
                                respMessageXml = new String(outStream
                                        .toByteArray(), "UTF-8");
                            } else {
                                // 500 表示返回失败，发生异常
                                respMessageData.append("响应500内容开始！\n");
                                respMessageData.append(new String(outStream
                                        .toByteArray())
                                        + "\n");
                                respMessageData.append("响应500内容结束！\n");
                                isSuccess = false;
                                errCode = String.valueOf(statusCode);
                                errDesc = new String(outStream.toByteArray());
                            }
                        }
                        /*** 4 接收通讯报文并处理 ** 结束 **/
                    } catch (IOException e) {
                        isSuccess = false;
                        errCode = String.valueOf(statusCode);
                        errDesc = e.getMessage();
                        log.error("读取认证响应报文出现异常！状态码：" + errCode + "失败原因：" + errDesc + "\n");
                    }
                }
            }
            /*** 1 创建与网关的HTTP连接 ** 结束 **/
            /**************************
             *第八步：服务端处理 *
             **************************/
            Document respDocument = null;
            Element headElement = null;
            Element bodyElement = null;
            if (isSuccess) {
                respDocument = DocumentHelper.parseText(respMessageXml);

                headElement = respDocument.getRootElement().element(MSG_HEAD);
                bodyElement = respDocument.getRootElement().element(MSG_BODY);
                /*** 1 解析报文头 ** 开始 **/
                if (headElement != null) {
                    boolean state = Boolean.valueOf(
                            headElement.elementTextTrim(MSG_MESSAGE_STATE))
                            .booleanValue();
                    if (state) {
                        isSuccess = false;
                        errCode = headElement.elementTextTrim(MSG_MESSAGE_CODE);
                        errDesc = headElement.elementTextTrim(MSG_MESSAGE_DESC);
                        log.error("认证业务处理失败！状态码：" + errCode + "失败原因:" + errDesc + "\n");
                    }
                }
            }
            if (isSuccess) {
                /* 解析报文体 */
                // 解析认证结果集
                Element authResult = bodyElement.element(MSG_AUTH_RESULT_SET)
                        .element(MSG_AUTH_RESULT);
                isSuccess = Boolean.valueOf(
                        authResult.attributeValue(MSG_SUCCESS)).booleanValue();
                if (!isSuccess) {
                    errCode = authResult
                            .elementTextTrim(MSG_AUTH_MESSSAGE_CODE);
                    errDesc = authResult
                            .elementTextTrim(MSG_AUTH_MESSSAGE_DESC);
                    log.error("身份认证失败，状态码："  + errCode + "失败原因：" + errDesc + "\n");
                }
            }
            if (isSuccess) {
                String ss = bodyElement.elementTextTrim("accessControlResult");
                // 解析用户属性列表
                Element attrsElement = bodyElement.element(MSG_ATTRIBUTES);
//				Map<String[], String> attributeNodeMap = new HashMap<String[], String>();
//				Map<String[], String> childAttributeNodeMap = new HashMap<String[], String>();
//				String[] keyes = new String[2];
                if (attrsElement != null) {
                    List attributeNodeList = attrsElement.elements(MSG_ATTRIBUTE);
                    for (int i = 0; i < attributeNodeList.size(); i++) {
//						keyes = new String[2];
                        Element userAttrNode = (Element) attributeNodeList.get(i);
                        //获取父节点
                        String msgParentName = userAttrNode.attributeValue(MSG_PARENT_NAME);
                        String name = userAttrNode.attributeValue(MSG_NAME);
                        String value = userAttrNode.getTextTrim();
                        attributeNodeMap.put(name, value);
						/*keyes[0]=name;
						if(msgParentName!=null && !msgParentName.equals("")){
							keyes[1]=msgParentName;
							childAttributeNodeMap.put(keyes, value);
						}else{
							attributeNodeMap.put(keyes, value);
						}*/
                    }
                }
            }
        } catch (Exception e) {
            isSuccess = false;
            e.printStackTrace();
        }
        return isSuccess;

    }

    public static boolean isNotEmpty(String str) {
        if (str != null) {
            if ("".equals(str.trim())) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
}
