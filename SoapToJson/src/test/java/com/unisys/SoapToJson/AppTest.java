package com.unisys.SoapToJson;

import java.io.StringWriter;

import javax.xml.soap.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.*;

public class AppTest {

	/**
	 * Starting point for the SAAJ - SOAP Client Testing
	 */
	public static void main(String args[]) {
		try {
			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			// String url =
			// "http://ws.cdyne.com/emailverify/Emailvernotestemail.asmx";

			String url = "http://192.61.111.23:2008/CF1DINF1/CFDINF1S_CUSTOMER_INFORMATION1";
			SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(), url);
			String result = "sdfd sdfsdf  we werW D CDE  code sdf sdfksf dsf code asd asd a";
			String[] inputArray = result.split("code");

			System.out.println();
			// Process the SOAP Response
			printSOAPResponse(soapResponse);

			soapConnection.close();
		} catch (Exception e) {
			System.err.println("Error occurred while sending SOAP Request to Server");
			e.printStackTrace();
		}
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
	/*
	 * private static void printSOAPResponse(SOAPMessage soapResponse) throws
	 * Exception { TransformerFactory transformerFactory =
	 * TransformerFactory.newInstance(); Transformer transformer =
	 * transformerFactory.newTransformer(); Source sourceContent =
	 * soapResponse.getSOAPPart().getContent(); System.out.print(
	 * "\nResponse SOAP Message = "); StreamResult result = new
	 * StreamResult(System.out); transformer.transform(sourceContent, result); }
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