package com.unisys.SoapToJson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Iterator;

import javax.xml.soap.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.*;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class SoapClientFinal {
	public static void main(String args[]) {
	/*	LambdaLogger logger = context.getLogger();
		logger.log("soapclient : ");
		String result;
		String userEnteredNumber = "";
		String password = "";
		String agentId = "";
		logger.log("\ninputStream before foramtting" + inputStream);
		try {
			result = IOUtils.toString(inputStream);
			String[] inputArray = result.split("userEnteredNumber");
			String[] inputArray1 = inputArray[1].split("\"");
			userEnteredNumber = inputArray1[2];
			inputArray = null;
			inputArray1 = null;
			inputArray = result.split("password");
			inputArray1 = inputArray[1].split("\"");
			password = inputArray1[2];
			inputArray = null;
			inputArray1 = null;
			inputArray = result.split("agentId");
			inputArray1 = inputArray[1].split("\"");
			agentId = inputArray1[2];
			logger.log("\ninputStream" + result);
			logger.log("\nuserEnteredNumber" + userEnteredNumber);
			logger.log("\npassword" + password);
			logger.log("\nagentId" + agentId);
		} catch (IOException e1) {
			logger.log("\nwhile formatting catch" + e1);
			e1.printStackTrace();
		}
*/
		try {
			// Create SOAP Connection 
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			// String url =
			// "http://192.61.111.23:2008/CF1DINF1/CFDINF1S_CUSTOMER_INFORMATION1";
			String url = "http://66.41.122.143:2008/CF1DINF1/CFDINF1S_CUSTOMER_INFORMATION1";
			SOAPMessage soapResponse = soapConnection.call(createSOAPRequest("1700000023", "4221", ""),
					url);
			// Process the SOAP Response
			String soapResponseStr = printSOAPResponse(soapResponse); 
			soapConnection.close();
			String jsonOutput = convertSoapToJson(soapResponseStr); 
			//System.out.println(jsonOutput);
			/*
			outputStream.write(jsonOutput.getBytes(Charset.forName("UTF-8")));*/
		//	return jsonOutput;
		 } catch (Exception e) {
			System.err.println("Error occurred while sending SOAP Request to Server");
			e.printStackTrace(); 
		} 
		//return "error while converting to json";
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
				System.out.println("inside json array" + child.get(key));
				JSONArray jsonArray = child.getJSONArray(key);
				System.out.println("json array" + jsonArray);
				JSONArray jsonArr = new JSONArray();
				for (int i = 0; i < jsonArray.length(); i++) {
				//	System.out.println("for" + jsonArray);
					testJObj1 = editKeys(jsonArray.getJSONObject(i), new JSONObject());
				//	System.out.println("testJObj1");
					//System.out.println(testJObj1);
					jsonArr.put(testJObj1);
				}
				parent.put(key, jsonArr);
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

	private static String convertSoapToJson(String soapResponseStr)
			throws Exception, JsonMappingException, IOException {
		JSONObject jObject = XML.toJSONObject(soapResponseStr);
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		Object json = mapper.readValue(jObject.toString(), Object.class);
		String output = mapper.writeValueAsString(json);
		String[] tokensVal = output.split("Cfdinf1sCustomerInformation1callResponse");
		tokensVal[1] = "{\"cfdinf1sCustomerInformation1callResponse" + tokensVal[1];
		String outputStr = tokensVal[1].substring(0, tokensVal[1].length() - 4);
		System.out.println(outputStr);
	 	outputStr=outputStr.replaceAll("\\s+","");
		outputStr = outputStr.replace("{\"row\":", "");
		outputStr = outputStr.replace("]}", "]");
		System.out.println(outputStr);
		JSONObject jObj1 = new JSONObject(outputStr);
		JSONObject child = jObj1.getJSONObject("cfdinf1sCustomerInformation1callResponse");
		child.remove("xmlns:ns2");
		JSONObject parent = new JSONObject();
		parent = editKeys(child, parent);
		Object convertedJson = mapper.readValue(parent.toString(), Object.class);
		String convertedStr = mapper.writeValueAsString(convertedJson);
		convertedStr = "{\"cfdinf1sCustomerInformation1callResponse\":" + convertedStr + "}";
	//	System.out.println(convertedStr);
		return convertedStr;
	}

	private static SOAPMessage createSOAPRequest(String userEnteredNumber, String pwd, String agentid)
			throws Exception {
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();
		SOAPPart soapPart = soapMessage.getSOAPPart();
		String serverURI = "http://tempuri.org/Cfdinf1sCustomerInformation1/";
		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration("cfd", serverURI);
		MimeHeaders headers = soapMessage.getMimeHeaders();
		headers.addHeader("SOAPAction", serverURI + "Cfdinf1sCustomerInformation1call");
		SOAPBody soapBody = envelope.getBody();
		SOAPElement customerInfoCall = soapBody.addChildElement("Cfdinf1sCustomerInformation1call", "cfd");
		SOAPElement customerInfoImport = soapBody.addChildElement("Cfdinf1sCustomerInformation1Import");
		SOAPElement customerNo = soapBody.addChildElement("ImportWkCustomerIdNumberAndAlias");
		SOAPElement userEnteredNo = soapBody.addChildElement("UserEnteredNumber");
		SOAPElement customerInterface = soapBody.addChildElement("ImportWkCustomerInterface");
		SOAPElement password = soapBody.addChildElement("Password");
		SOAPElement agentId = soapBody.addChildElement("AgentId");
		customerNo.addChildElement(userEnteredNo);
		customerInterface.addChildElement(password);
		customerInterface.addChildElement(agentId);
		customerInfoImport.addChildElement(customerInterface);
		customerInfoImport.addChildElement(customerNo);
		customerInfoCall.addChildElement(customerInfoImport);
		agentId.addTextNode(agentid);
		password.addTextNode(pwd);
		userEnteredNo.addTextNode(userEnteredNumber);
		customerInfoImport.setAttribute("command", "DISPLAY");
		customerInfoImport.setAttribute("clientId", "?");
		customerInfoImport.setAttribute("clientPassword", "?");
		customerInfoImport.setAttribute("nextLocation", "?");
		customerInfoImport.setAttribute("exitState", "?");
		customerInfoImport.setAttribute("dialect", "?");
		soapMessage.saveChanges();
		/* Print the request message */
		System.out.print("Request SOAP Message = ");
		soapMessage.writeTo(System.out);
	//	System.out.println();

		return soapMessage;
	}

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