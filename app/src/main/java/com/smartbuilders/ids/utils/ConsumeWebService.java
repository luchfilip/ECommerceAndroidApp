package com.smartbuilders.ids.utils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.LinkedHashMap;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * before 27.02.2016
 * @author jsarco
 *
 */
public class ConsumeWebService {
	
	private static final String TAG 				= ConsumeWebService.class.getSimpleName();
	public static final String SHOW_TOAST_MESSAGE 	= "com.jasgcorp.ids.utlis.ConsumeWebService.SHOW_TOAST_MESSAGE";
	public static final String MESSAGE 				= "com.jasgcorp.ids.utlis.ConsumeWebService.MESSAGE";
	private static final String NAMESPACE 			= "http://webservices.ids.jasgcorp.com";
	private final int MAX_RETRY_NUMBER 				= 3;
	private final int HTTP_TRANSPORT_TIMEOUT 		= 30*1000;//in milliseconds
	
	//Constantes para la invocacion del web service
	private Context context;
	private String serverAddress;
	private String url;
	private String methodName;
	private String soapAction;
	private LinkedHashMap<String, Object> parameters;
	private int retryNumber;
	private int connectionTimeOut;

	public ConsumeWebService(Context context, String serverAddress, String url,
							 String methodName, String soapAction, LinkedHashMap<String, Object> parameters, int connectionTimeOut){
		this(context, serverAddress, url, methodName, soapAction, parameters);
		this.connectionTimeOut = connectionTimeOut;
	}

	public ConsumeWebService(Context context, String serverAddress, String url, 
			String methodName, String soapAction, LinkedHashMap<String, Object> parameters){
		this.context 		= context;
		this.serverAddress 	= serverAddress;
		this.url 			= url;
		this.methodName 	= methodName;
		this.soapAction 	= soapAction;
		this.parameters 	= parameters;
	}
	
	public Object getWSResponse() throws Exception {
		SoapObject request;
		SoapSerializationEnvelope envelope;
		//Objeto que representa el modelo de transporte
		//Recibe la URL del ws
		HttpTransportSE transporte = new HttpTransportSE(serverAddress+url,
				connectionTimeOut>0 ? connectionTimeOut : HTTP_TRANSPORT_TIMEOUT);
		Object response = null;
		try {
			//Hace la llamada al ws
			//long timeBefore = System.currentTimeMillis();
			
			//Se crea un objeto SoapObject para poder realizar la peticion
			//para consumir el ws SOAP. El constructor recibe
			//el namespace. Por lo regular el namespace es el dominio
			//donde se encuentra el web service
			request = new SoapObject(NAMESPACE, methodName);
			for(String parameterName : this.parameters.keySet()){
				request.addProperty(parameterName, this.parameters.get(parameterName)); // Paso parametros al WS
			}

			//Se crea un objeto SoapSerializationEnvelope para serealizar la
			//peticion SOAP y permitir viajar el mensaje por la nube
			//el constructor recibe la version de SOAP
			envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true; //se asigna true para el caso de que el WS sea de dotNet
			
			//Se envuelve la peticion soap
			envelope.setOutputSoapObject(request);
			transporte.call(soapAction, envelope);
			response = envelope.getResponse();
			//Log.i(TAG, "transport time: "+(System.currentTimeMillis() - timeBefore)+"ms");
        } catch(ConnectException e){
			Log.e(TAG, "ConnectException");
        	//e.printStackTrace();
        	return retry(e);
        } catch(SocketTimeoutException e){
			Log.e(TAG, "SocketTimeoutException");
			//e.printStackTrace();
			return retry(e);
        } catch(SocketException e){
			Log.e(TAG, "SocketException");
        	//e.printStackTrace();
        	return retry(e);
        } catch (MalformedURLException e) {
			Log.e(TAG, "MalformedURLException");
			//e.printStackTrace();
			throw e;
			//return retry(e);
		} catch(IOException e){
			Log.e(TAG, "IOException");
        	//e.printStackTrace();
            throw e;
       		//return retry(e);
        } catch(Exception e){
			Log.e(TAG, "Exception");
			//e.printStackTrace();
			throw e;
		}
		return response;
	}

	private Object retry(Exception exception) throws Exception{
    	if(++retryNumber <= MAX_RETRY_NUMBER){
    		try {
    			Log.e(TAG, "Retry in "+((2*retryNumber)*1000)+" milliseconds.");
    			context.sendBroadcast((new Intent(SHOW_TOAST_MESSAGE))
    					.putExtra(MESSAGE, "Failed to communicate with server, retry in "+((2*retryNumber)*1000)+" milliseconds."));
                //Se incrementa el tiempo de timeOut
				connectionTimeOut += connectionTimeOut/3;
    		    Thread.sleep((2*retryNumber)*1000);
    		} catch(InterruptedException ex) {
    		    Thread.currentThread().interrupt();
    		}
    		return getWSResponse();
    	}
    	throw exception;
	}
}
