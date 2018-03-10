package com.unisys.SoapToJson;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.soap.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.*;

import org.json.JSONObject;
import org.json.XML;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class TestSoapClient {
	public String myHandler(int a, Context context) {
		LambdaLogger logger = context.getLogger();
		logger.log("in test url soapclient : ");
		logger.log("a" + a);
		try {
			// Create SOAP Connection
			logger.log("inside try : ");
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			logger.log("soapConnection : " + soapConnection);
			String url = "http://ws.cdyne.com/emailverify/Emailvernotestemail.asmx";
			SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(), url);
			logger.log("soapResponse : " + soapResponse);
			// Process the SOAP Response
			String soapResponseStr = printSOAPResponse(soapResponse);
			logger.log("soapResponseStr : " + soapResponseStr);
			soapConnection.close();
			String jsonOutput = convertSoapToJson(soapResponseStr);
			logger.log("json format : " + jsonOutput);
			return jsonOutput;
		} catch (Exception e) {
			System.err.println("Error occurred while sending SOAP Request to Server");
			e.printStackTrace();
			logger.log("error : " + e.getMessage());
		}
		return "error while converting to json";
	}

	private static String convertSoapToJson(String soapResponseStr)
			throws Exception, JsonMappingException, IOException {
		// String xml = IOUtils.toString(inputStream);
		JSONObject jObject = XML.toJSONObject(soapResponseStr);
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		Object json = mapper.readValue(jObject.toString(), Object.class);
		String output = mapper.writeValueAsString(json);
		System.out.println(output);
		return output;
	}

	private static SOAPMessage createSOAPRequest() throws Exception {
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();
		SOAPPart soapPart = soapMessage.getSOAPPart();

		String serverURI = "http://ws.cdyne.com/";

		// SOAP Envelope
		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration("example", serverURI);
		SOAPBody soapBody = envelope.getBody();
		SOAPElement soapBodyElem = soapBody.addChildElement("VerifyEmail", "example");
		SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("email", "example");
		soapBodyElem1.addTextNode("mutantninja@in.unisys.com");
		SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("LicenseKey", "example");
		soapBodyElem2.addTextNode("123");

		MimeHeaders headers = soapMessage.getMimeHeaders();
		headers.addHeader("SOAPAction", serverURI + "VerifyEmail");

		soapMessage.saveChanges();

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