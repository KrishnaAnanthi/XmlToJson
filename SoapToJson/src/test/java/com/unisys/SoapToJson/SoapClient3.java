package com.unisys.SoapToJson;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.Map;

import javax.xml.soap.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.*;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class SoapClient3 {
	public static void main(String args[]) {
		try {
			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			// String url =
			// "http://ws.cdyne.com/emailverify/Emailvernotestemail.asmx";
			String url = "http://192.61.111.23:2008/CF1DINF1/CFDINF1S_CUSTOMER_INFORMATION1";

			// String url =
			// "http://66.41.122.143:2008/CF1DINF1/CFDINF1S_CUSTOMER_INFORMATION1";
			SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(), url);

			// Process the SOAP Response
			String soapResponseStr = printSOAPResponse(soapResponse);

			soapConnection.close();
			convertSoapToJson(soapResponseStr);
		} catch (Exception e) {
			System.err.println("Error occurred while sending SOAP Request to Server");
			e.printStackTrace();
		}
	}

	private static JSONObject editKeys(JSONObject child, JSONObject parent) {
		Iterator<?> keys = child.keys();
		JSONObject testJObj1;
		while (keys.hasNext()) {
			String key = (String) keys.next();
			String lowerCaseKey = key.substring(0, 1).toLowerCase() + key.substring(1);
			if (child.get(key) instanceof JSONObject) {
				JSONObject jsonObj = new JSONObject();
				testJObj1 = editKeys(child.getJSONObject(key), jsonObj);
				parent.put(lowerCaseKey, jsonObj);
			} else if (child.get(key) instanceof JSONArray) {
				JSONArray jsonArray = new JSONArray(child.get(key));
				JSONArray jsonArr = new JSONArray();
				for (int i = 0; i < jsonArray.length(); i++) {
					testJObj1 = editKeys(jsonArray.getJSONObject(i), new JSONObject());
					jsonArr.put(testJObj1);
				}
				parent.put(lowerCaseKey, jsonArr);
			} else if (child.get(key) instanceof String) {
				parent.put(lowerCaseKey, child.getString(key));

			} else if (child.get(key) instanceof Integer) {
				parent.put(lowerCaseKey, child.getInt(key));

			} else if (child.get(key) instanceof Double) {
				parent.put(lowerCaseKey, child.getDouble(key));

			} else {
				parent.put(lowerCaseKey, child.get(key));
			}
		}
		return parent;
	}

	private static void convertSoapToJson(String soapResponseStr) throws Exception, JsonMappingException, IOException {
		// String xml = IOUtils.toString(inputStream);
		JSONObject jObject = XML.toJSONObject(soapResponseStr);
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		Object json = mapper.readValue(jObject.toString(), Object.class);
		String output = mapper.writeValueAsString(json);
		String[] tokensVal = output.split("Cfdinf1sCustomerInformation1callResponse");
		tokensVal[1] = "{\"cfdinf1sCustomerInformation1callResponse" + tokensVal[1];
		String outputStr = tokensVal[1].substring(0, tokensVal[1].length() - 4);

		outputStr = outputStr.replace("{\n \"outTextDefaultDate\":", "test");
		JSONObject jObj1 = new JSONObject(outputStr);
		JSONObject child = jObj1.getJSONObject("cfdinf1sCustomerInformation1callResponse");
		child.remove("xmlns:ns2");
		JSONObject parent = new JSONObject();
		parent = editKeys(child, parent);
		Object jObj2 = mapper.readValue(parent.toString(), Object.class);
		String convertedStr = mapper.writeValueAsString(jObj2);
		convertedStr = "{\"cfdinf1sCustomerInformation1callResponse\":" + convertedStr + "}";
		System.out.println(convertedStr);

		String test = "{\"cfdinf1sCustomerInformation1call\": {\"cfdinf1sCustomerInformation1Import\": {\"importWkCustomerIdNumberAndAlias\": {\"userEnteredNumber\": \"1100000021\" },\"importWkCustomerInterface\": { \"password\": \"\", \"agentId\": \"\"}}}} ";
		test=test.replaceAll("\\s+","");
		test = test.replace("{\"password\":", "test");
		System.out.println(test);

		/*
		 * Iterator<?> keys = jObj.keys(); while (keys.hasNext()) { String key =
		 * (String) keys.next(); System.out.println("outside if key is " + key +
		 * "value is" + jObj.get(key) + "\n if cond " + (jObj.get(key)
		 * instanceof JSONObject)); if (jObj.get(key) instanceof JSONObject) {
		 * Iterator<?> childKeys = new JSONObject(jObj.get(key)).keys();
		 * System.out.println(childKeys + "hasnext cond" + childKeys.hasNext());
		 * while (childKeys.hasNext()) { String childkey = (String)
		 * childKeys.next(); System.out.println("if key" + childkey); } } if
		 * (jObj.get(key) instanceof JSONArray) {
		 * 
		 * for (int i = 0; i < jsonArray.length(); i++) {
		 * System.out.println(jsonArray.getJSONObject(i).getString("id") ); } }
		 * System.out.println("key" + key); }
		 */
		// return outputStr;
		/*
		 * String test=
		 * "{\"cfdinf1sCustomerInformation1call\": {\"cfdinf1sCustomerInformation1Import\": {\"importWkCustomerIdNumberAndAlias\": {\"userEnteredNumber\": \"1100000021\" },\"importWkCustomerInterface\": { \"password\": \"\", \"agentId\": \"\"}}}} "
		 * ;
		 * 
		 * String[] inputArray = test.split("userEnteredNumber"); String[]
		 * inputArray1 = inputArray[1].split("\"");
		 * System.out.println(inputArray1[2]); inputArray=null;inputArray1=null;
		 * inputArray = test.split("password");
		 * System.out.println(inputArray[1]); inputArray1 =
		 * inputArray[1].split("\"");
		 * System.out.println(inputArray1[2]);inputArray=null;inputArray1=null;
		 * inputArray = test.split("agentId");
		 * System.out.println(inputArray[1]); inputArray1 =
		 * inputArray[1].split("\""); System.out.println(inputArray1[2]);
		 * 
		 * String[] tokensVal =
		 * output.split("Cfdinf1sCustomerInformation1callResponse");
		 * tokensVal[1] = "{\"Cfdinf1sCustomerInformation1callResponse" +
		 * tokensVal[1]; //System.out.println(tokensVal[1]); String outputJson =
		 * tokensVal[1].substring(0, tokensVal[1].length() - 4);
		 */
		// System.out.println(outputJson);
	}

	private static SOAPMessage createSOAPRequest() throws Exception {

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();
		SOAPPart soapPart = soapMessage.getSOAPPart();

		/*
		 * String serverURI = "http://ws.cdyne.com/";
		 * 
		 * // SOAP Envelope SOAPEnvelope envelope = soapPart.getEnvelope();
		 * envelope.addNamespaceDeclaration("example", serverURI); SOAPBody
		 * soapBody = envelope.getBody(); SOAPElement soapBodyElem =
		 * soapBody.addChildElement("VerifyEmail", "example"); SOAPElement
		 * soapBodyElem1 = soapBodyElem.addChildElement("email", "example");
		 * soapBodyElem1.addTextNode("mutantninja@gmail.com"); SOAPElement
		 * soapBodyElem2 = soapBodyElem.addChildElement("LicenseKey",
		 * "example"); soapBodyElem2.addTextNode("123");
		 * 
		 * MimeHeaders headers = soapMessage.getMimeHeaders();
		 * headers.addHeader("SOAPAction", serverURI + "VerifyEmail");
		 * 
		 * soapMessage.saveChanges();
		 */
		/*
		 * Constructed SOAP Request Message: <SOAP-ENV:Envelope
		 * xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
		 * xmlns:example="http://ws.cdyne.com/"> <SOAP-ENV:Header/>
		 * <SOAP-ENV:Body> <example:VerifyEmail>
		 * <example:email>mutantninja@gmail.com</example:email>
		 * <example:LicenseKey>123</example:LicenseKey> </example:VerifyEmail>
		 * </SOAP-ENV:Body> </SOAP-ENV:Envelope>
		 */
		String serverURI = "http://tempuri.org/Cfdinf1sCustomerInformation1/";
		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration("cfd", serverURI);
		MimeHeaders headers = soapMessage.getMimeHeaders();
		headers.addHeader("SOAPAction", serverURI + "Cfdinf1sCustomerInformation1call");
		SOAPBody soapBody = envelope.getBody();
		SOAPElement soapBodyElem1 = soapBody.addChildElement("Cfdinf1sCustomerInformation1call", "cfd");
		SOAPElement soapBodyElem2 = soapBody.addChildElement("Cfdinf1sCustomerInformation1Import", "cfd");
		SOAPElement soapBodyElem3 = soapBody.addChildElement("ImportWkCustomerIdNumberAndAlias", "cfd");
		SOAPElement soapBodyElem4 = soapBody.addChildElement("UserEnteredNumber", "cfd");
		SOAPElement soapBodyElem5 = soapBody.addChildElement("ImportWkCustomerInterface", "cfd");
		SOAPElement soapBodyElem6 = soapBody.addChildElement("Password", "cfd");
		SOAPElement soapBodyElem7 = soapBody.addChildElement("AgentId", "cfd");
		soapBodyElem6.addTextNode("4221");
		soapBodyElem4.addTextNode("1100000021");
		soapBodyElem2.setAttribute("command", "DISPLAY");
		soapBodyElem2.setAttribute("clientId", "?");
		soapBodyElem2.setAttribute("clientPassword", "?");
		soapBodyElem2.setAttribute("nextLocation", "?");
		soapBodyElem2.setAttribute("exitState", "?");
		soapBodyElem2.setAttribute("dialect", "?");
		soapMessage.saveChanges();

		/*
		 * <soapenv:Envelope
		 * xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
		 * xmlns:cfd="http://tempuri.org/Cfdinf1sCustomerInformation1/">
		 * <soapenv:Header/> <soapenv:Body>
		 * <cfd:Cfdinf1sCustomerInformation1call>
		 * <Cfdinf1sCustomerInformation1Import command="DISPLAY" clientId="?"
		 * clientPassword="?" nextLocation="?" exitState="?" dialect="?">
		 * <ImportWkCustomerIdNumberAndAlias>
		 * <UserEnteredNumber>1100000021</UserEnteredNumber>
		 * </ImportWkCustomerIdNumberAndAlias> <ImportWkCustomerInterface>
		 * <Password>4221</Password> <AgentId>?</AgentId>
		 * </ImportWkCustomerInterface> </Cfdinf1sCustomerInformation1Import>
		 * </cfd:Cfdinf1sCustomerInformation1call> </soapenv:Body>
		 * </soapenv:Envelope>
		 */
		// SOAP Body

		/* Print the request message */
		System.out.print("Request SOAP Message = ");
		soapMessage.writeTo(System.out);
		System.out.println();

		return soapMessage;
	}

	/**
	 * Method used to print the SOAP Response
	 */
	private static String printSOAPResponse(SOAPMessage soapResponse) throws Exception {
		final StringWriter sw = new StringWriter();
		try {
			TransformerFactory.newInstance().newTransformer().transform(new DOMSource(soapResponse.getSOAPPart()),
					new StreamResult(sw));
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}
		System.out.println(sw.toString());
		return sw.toString();
	}

}