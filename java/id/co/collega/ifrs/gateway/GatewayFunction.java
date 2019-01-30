package id.co.collega.ifrs.gateway;

import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

import id.co.collega.ifrs.common.Cryptograph;
import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.v7.ef.common.Utils;

@Service
public class GatewayFunction {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	Gson gson;
	@Autowired Environment env;
//	@Autowired FeeProductRepository feeProductRepository;
//	@Autowired BankingLogRepository logRepository;
//	@Autowired ProductRepository productRepository;
	@Autowired RestTemplate restTemplate;
	
	public static void main(String argsp[]){
		System.out.println(Cryptograph.getInstance().encryptText("password"));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean sendMessageGoogle(String phone, String text){
		try {
			String API_KEY = "AIzaSyCCcDIBIjPnYJF0oXQHilv3ITAzm2mCGBo";
			String endPoint = UriComponentsBuilder.fromUriString("https://android.googleapis.com/gcm/send").toUriString();
			logger.info("Send sms to {} to {}", phone, endPoint);

			HttpHeaders header = new HttpHeaders();
	    	List<org.springframework.http.MediaType> acceptableMediaTypes = new ArrayList<org.springframework.http.MediaType>();
	        acceptableMediaTypes.add(org.springframework.http.MediaType.APPLICATION_JSON);
	        header.setAccept(acceptableMediaTypes);
	        header.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
	        header.add("Authorization", "key=" + API_KEY);
	        
			Map<String, Object> parameter = new HashMap<String, Object>();
	        Map<String, Object> data = new HashMap<String, Object>();
	        data.put("message", text);
	        data.put("sendToPhoneNumber", phone);
	        parameter.put("to", "/topics/global");
	        parameter.put("data", data);
			ResponseEntity<String> responseEntity = restTemplate.exchange(
					endPoint, HttpMethod.POST, new HttpEntity(parameter, header), String.class); 
			logger.info(responseEntity.getBody());
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}
	
	public JSONObject createRequest(String reqId){
		try {
			JSONObject request = new JSONObject();
			Date date = new Date();
			request.put("reqId", reqId);
			request.put("txDate", (new SimpleDateFormat("yyyyMMdd")).format(date));
			request.put("txHour", (new SimpleDateFormat("HHmmss")).format(date));
			request.put("userGtw", env.getRequiredProperty("user.gateway"));
			request.put("passwordGtw", Cryptograph.getInstance().encryptText(env.getRequiredProperty("pass.gateway")));
			request.put("channelId", env.getRequiredProperty("channel.gateway"));
			
			return request;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new RuntimeException("Terjadi kesalahan pada system");
		}
	}
	
	public WebResource createWebResourcePost(){
		try {
			HostnameVerifier hv = Utils.getHostnameVerifier();
			SSLContext ctx = Utils.getSSLContext();
			ClientConfig config = new DefaultClientConfig();
			config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(hv, ctx));
			Client client = Client.create(config);
			client.setConnectTimeout(60 * 1000);
			client.setReadTimeout(60 * 1000);
			return client.resource(env.getRequiredProperty("url.gateway") + 
					"Gateway/gateway/services/postDataExt");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException("Terjadi kesalahan pada system");
		}
	}
	
	public WebResource createWebResourceGet(String url){
		try {
			
			
			HostnameVerifier hv = Utils.getHostnameVerifier();
			SSLContext ctx = Utils.getSSLContext();
			ClientConfig config = new DefaultClientConfig();
			config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(hv, ctx));
			Client client = Client.create(config);
			client.setConnectTimeout(60 * 1000);
			client.setReadTimeout(60 * 1000);
			return client.resource(url);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException("Terjadi kesalahan pada system");
		}
	}
	
	//add 22/11/2017
	public DTOMap loginUserCore(String userId, String password){
		try {
				
			JSONObject request = createRequest("00001");
			Date date = new Date();
//			request.put("reqId", "00001");
//			request.put("txDate", (new SimpleDateFormat("yyyyMMdd")).format(date));
//			request.put("txHour", (new SimpleDateFormat("HHmmss")).format(date));
//			request.put("userGtw", userGtw);
//			request.put("passwordGtw", Cryptograph.getInstance().encryptText(passwordGtw));
//			request.put("channelId", "21");
			
			request.put("userId", userId);
			request.put("password", Cryptograph.getInstance().encryptText(password));
			
			WebResource webResource = createWebResourcePost();
			ClientResponse clientResponse = webResource.post(ClientResponse.class, request.toString());
			logger.info("Request [{}] [{}]", webResource.getURI().toURL(), request.toString());
			
			if (clientResponse.getStatus() != 200) {
				logger.error("Failed : Response from HTTP : " + clientResponse.getStatus());
				throw new RuntimeException("Failed : Response from HTTP : " + clientResponse.getStatus());
			}else{
				String respon = clientResponse.getEntity(String.class);
				logger.info("Response from gateway[{}]", respon);
			
				try {
					DTOMap rs = jsonDTOMap(respon);		
					DTOMap result = new DTOMap();
					DTOMap data = (DTOMap) rs.get("result");
					System.out.println("login user core "+data);
					if (rs.getInt("statusId").equals(1)) {
						result.put("result", data);
						result.put("statusId", rs.getInt("statusId"));
						result.put("pesan", rs.get("message"));
						return result;
					}else {
						
						result.put("statusId", rs.getInt("statusId"));
						result.put("pesan", rs.get("message"));
						return result;
					}
				
				} catch (RuntimeException e) {
					logger.error(e.getMessage(), e);
					throw new RuntimeException(e.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					throw new RuntimeException(e.getMessage());
				}
			}
		}catch (JSONException | MalformedURLException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException("Terjadi kesalahan pada system");
		}
	}
	
	public DTOMap updateResiko(DTOMap dto){
		try {
			JSONObject request = createRequest("00071");
			request.put("cifId", dto.getString("CIFID"));
			request.put("stsResiko", dto.getInt("KODE_BOBOT"));
			System.out.println("sts resiko ---- "+dto.getInt("KODE_BOBOT"));
		
			WebResource webResource = createWebResourcePost();
			ClientResponse clientResponse = webResource.post(ClientResponse.class, request.toString());
			logger.info("Request [{}] [{}]", webResource.getURI().toURL(), request.toString());
			if (clientResponse.getStatus() != 200) {
				logger.error("Failed : Response from gateway [{}]", clientResponse.getStatus());
				throw new RuntimeException("Failed : Response from gateway : " + clientResponse.getStatus());
			}else{
				String respon = clientResponse.getEntity(String.class);
				logger.info("Response from gateway[{}]", respon);
				System.out.println("update resiko "+respon);
				try {
					DTOMap rs = jsonDTOMap(respon);		
					DTOMap result = new DTOMap();
					DTOMap data = (DTOMap) rs.get("result");
					if (rs.getInt("statusId").equals(1)) {
						result.put("statusId", rs.getInt("statusId"));
						result.put("pesan", rs.get("message"));
						return result;
					}else {
						
						result.put("statusId", rs.getInt("statusId"));
						result.put("pesan", rs.get("message"));
						return result;
					}
				
				} catch (RuntimeException e) {
					logger.error(e.getMessage(), e);
					throw new RuntimeException(e.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					throw new RuntimeException(e.getMessage());
				}
			}
		} catch (JSONException | MalformedURLException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException("Terjadi kesalahan pada system");
		}
	}
	
	
	public DTOMap getAccountsByCIF(String cifid){
		try {
			//00035
			JSONObject request = createRequest("00067");
			request.put("cifId", cifid);
			
			
			WebResource webResource = createWebResourcePost();
			ClientResponse clientResponse = webResource.post(ClientResponse.class, request.toString());
			logger.info("Request [{}] [{}]", webResource.getURI().toURL(), request.toString());
//			System.out.println("AAAAAAAAAAAAAAAAAAAa"+clientResponse.getStatus());
			if (clientResponse.getStatus() != 200) {
				logger.error("Failed : Response from gateway [{}]", clientResponse.getStatus());
				throw new RuntimeException("Failed : Response from gateway : " + clientResponse.getStatus());
			}else{
				String respon = clientResponse.getEntity(String.class);
				logger.info("Response from gateway[{}]", respon);
				System.out.println("AAAAAAAAAAAAAAAAAAAa"+respon);
				try {
					DTOMap rs = jsonDTOMap(respon);		
					DTOMap result = new DTOMap();
					DTOMap data = (DTOMap) rs.get("result");
					Date tanggalDefault = new SimpleDateFormat("yyyy-MM-dd").parse("1900-01-01");
					if (rs.getInt("statusId").equals(1)) {
						String pattern = "yyyy-MM-dd";
						SimpleDateFormat format = new SimpleDateFormat(pattern);
						if(data.getInt("CIFTYPE").equals(0)){
							result.put("CIFTYPE", data.getInt("CIFTYPE"));
							result.put("TYPEID", data.getString("TYPEID"));
							result.put("IDNBR", data.getString("IDNBR"));
							result.put("FULLNM", data.getString("FULLNM"));
							result.put("ALIAS", data.getString("ALIAS"));
							result.put("SURENM", data.getString("SURENM"));
							result.put("PARM_NEGARA", data.getString("PARM_NEGARA"));
							result.put("PARM_PROPINSI", data.getString("PARM_PROPINSI"));
							result.put("PARM_KABUPATEN", data.getString("PARM_KABUPATEN"));
							result.put("PARM_KECAMATAN", data.getString("PARM_KECAMATAN"));
							result.put("PARM_KELURAHAN", data.getString("PARM_KELURAHAN"));
							result.put("SETOR_AWAL", data.getString("SETOR_AWAL"));
							result.put("FREKUENSI_SETOR", data.getString("FREQMNTHDEP"));
							result.put("FREKUENSI_TARIK", data.getString("FREQMNTHWD"));
							result.put("KD_PEKERJAAN", data.getString("JOBID"));
							result.put("KD_JABATAN", data.getString("FUNCJOB"));
							result.put("KD_JNS_USH", data.getString("BUSSID"));
							result.put("KD_NM_USH", data.getString("COMNMJOB"));
							result.put("KD_LOKASI", data.getString("LOCJOB"));
							result.put("TAXHAVEN", data.getInt("TAXHAVEN"));
							result.put("KD_KGT", data.getString("KD_KGT"));
							result.put("KD_INF", data.getString("OTHERINFO"));
							result.put("STSPEP", data.get("STSPEP")==null?0:data.getInt("STSPEP"));
							result.put("STS_ID", data.getInt("STS_ID"));
							result.put("ALAMAT", data.getString("ADDR"));
							result.put("TGL_EXPIRED", data.getString("EXPDT")!=null?new SimpleDateFormat("yyyy-MM-dd").parse(data.getString("EXPDT")):"1900-01-01");
							
								
						}else{
							result.put("CIFTYPE", data.getInt("CIFTYPE"));
							result.put("STSPEP", data.get("STSPEP")==null?0:Integer.valueOf(data.getString("STSPEP")));
							result.put("NAMA_PERUSAHAAN", data.getString("NAMA_PERUSAHAAN"));
							result.put("JENIS_NON_PERORANGAN", data.getString("JENIS_NON_PERORANGAN"));
							result.put("BENTUK_BADAN", data.getString("BENTUK_BADAN"));
							result.put("NPWP", data.getString("NPWP"));						
							result.put("ALAMAT_AKTE", data.get("ALAMAT_AKTE")==null?0:data.getInt("ALAMAT_AKTE"));
							result.put("TGL_PEMBUKAAN", data.get("TGL_PEMBUKAAN")!=null?new SimpleDateFormat("yyyy-MM-dd").parse(data.getString("TGL_PEMBUKAAN")):tanggalDefault);	
							result.put("AKTE_PENDIRIAN", data.getString("AKTE_PENDIRIAN"));
							result.put("TGL_PENERBITAN", data.getString("TGL_PENERBITAN")!=null?new SimpleDateFormat("yyyy-MM-dd").parse(data.getString("TGL_PENERBITAN")):tanggalDefault);
							result.put("PENGESAHAN_HUKUM_ID", data.getString("PENGESAHAN_HUKUM_ID"));
							result.put("PENGESAHAN_HUKUM_EXPDT", data.get("PENGESAHAN_HUKUM_EXPDT")==null?tanggalDefault:new SimpleDateFormat("yyyy-MM-dd").parse(data.getString("PENGESAHAN_HUKUM_EXPDT")));
							result.put("PENGESAHAN_HUKUM_EXPENDDT", data.get("PENGESAHAN_HUKUM_EXPENDDT")!=null?new SimpleDateFormat("yyyy-MM-dd").parse(data.getString("PENGESAHAN_HUKUM_EXPENDDT")):tanggalDefault);
							result.put("BERITA_NEGARA_ID", data.getString("BERITA_NEGARA_ID"));
							result.put("BERITA_NEGARA_EXPDT", data.get("BERITA_NEGARA_EXPDT")!=null?new SimpleDateFormat("yyyy-MM-dd").parse(data.getString("BERITA_NEGARA_EXPDT")):tanggalDefault);
							result.put("BERITA_NEGARA_EXPENDDT", data.get("BERITA_NEGARA_EXPENDDT")!=null?new SimpleDateFormat("yyyy-MM-dd").parse(data.getString("BERITA_NEGARA_EXPENDDT")):tanggalDefault);
							result.put("SIUP_ID", data.getString("SIUP_ID"));
							result.put("SIUP_EXPDT", data.get("SIUP_EXPDT")!=null?new SimpleDateFormat("yyyy-MM-dd").parse(data.getString("SIUP_EXPDT")):tanggalDefault);
							result.put("SIUP_EXPENDDT", data.get("SIUP_EXPENDDT")!=null?new SimpleDateFormat("yyyy-MM-dd").parse(data.getString("SIUP_EXPENDDT")):tanggalDefault);	
							result.put("TDP_ID", data.getString("TDP_ID"));
							result.put("TDP_EXPDT", data.get("TDP_EXPDT")!=null?new SimpleDateFormat("yyyy-MM-dd").parse(data.getString("TDP_EXPDT")):tanggalDefault);						
							result.put("TDP_EXPENDDT", data.get("TDP_EXPENDDT")!=null?new SimpleDateFormat("yyyy-MM-dd").parse(data.getString("TDP_EXPENDDT")):tanggalDefault);			
							result.put("LOKASI_USAHA", data.get("LOKASI_USAHA")!=null?data.getString("LOKASI_USAHA"):"");
							result.put("STRUKTUR_KEPEMILIKAN", data.get("STRUKTUR_KEPEMILIKAN")!=null?data.getString("STRUKTUR_KEPEMILIKAN"):"");
							result.put("STATUS_PENGURUS", data.get("STATUS_PENGURUS")!=null?data.getString("STATUS_PENGURUS"):"");
							result.put("DOMISILI", data.get("DOMISILI")!=null?data.getString("DOMISILI"):"");
							result.put("TUJUAN_BUKA_REK", data.get("TUJUAN_BUKA_REK")!=null?data.getString("TUJUAN_BUKA_REK"):"");
							result.put("SUMBER_DANA", data.get("SUMBER_DANA")!=null?data.getString("SUMBER_DANA"):"");
							result.put("MANFAAT_PEMBUKAAN_REK", data.get("MANFAAT_PEMBUKAAN_REK")!=null?data.getString("MANFAAT_PEMBUKAAN_REK"):"");
							result.put("FREK_SETOR_BLN", data.get("FREK_SETOR_BLN")!=null?data.getString("FREK_SETOR_BLN"):"");
							result.put("FREK_TARIK_BLN", data.get("FREK_TARIK_BLN")!=null?data.getString("FREK_TARIK_BLN"):"");
							result.put("KEGIATAN_USAHA", data.get("KEGIATAN_USAHA")!=null?data.getString("KEGIATAN_USAHA"):"");						
							result.put("STATUS_B0", data.get("STATUS_B0")!=null?data.getString("STATUS_B0"):"0");
							result.put("ID_BO", data.get("ID_BO")!=null?data.getString("ID_BO"):"");
							result.put("NO_IDENTITAS_BO", data.get("NO_IDENTITAS_BO")!=null?data.getString("NO_IDENTITAS_BO"):"");
							result.put("SUMBER_DANA_BO", data.get("SUMBER_DANA_BO")!=null?data.getString("SUMBER_DANA_BO"):"");
							result.put("STS_PENGURUS_BO", data.get("STATUS_B0")!=null?data.getString("STS_PENGURUS_BO"):"");
							result.put("KEWARGANEGARAAN_BO", data.get("KEWARGANEGARAAN_BO")!=null?data.getString("KEWARGANEGARAAN_BO"):"");
							result.put("JUMLAH_WNA_BO", data.get("JUMLAH_WNA_BO")!=null?data.getInt("JUMLAH_WNA_BO").toString():"0");
							result.put("JUMLAH_KITAS_BO", data.get("JUMLAH_KITAS_BO")!=null?data.getInt("JUMLAH_KITAS_BO").toString():"0");
							result.put("KD_INF", data.getString("KD_INF"));
							
						}
						result.put("statusId", rs.getInt("statusId"));
						result.put("pesan", rs.get("message"));
						return result;
					}else {
						
						result.put("statusId", rs.getInt("statusId"));
						result.put("pesan", rs.get("message"));
						return result;
					}
				
				} catch (RuntimeException e) {
					logger.error(e.getMessage(), e);
					throw new RuntimeException(e.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					throw new RuntimeException(e.getMessage());
				}
			}
		} catch (JSONException | MalformedURLException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException("Terjadi kesalahan pada system");
		}
	}
	
	@SuppressWarnings("unchecked")
	public static DTOMap jsonDTOMap(String string) throws Exception{
		DTOMap result = new DTOMap();
		JSONObject jo = new JSONObject(string);
		Iterator<String> keys = jo.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			try {
				Object object = jo.get(key);
				if (object instanceof JSONObject) {
					JSONObject json = (JSONObject) object;
					object = jsonDTOMap(json.toString());
				}else if (object instanceof JSONArray) {
					List<DTOMap> listMap = new ArrayList<DTOMap>();
					JSONArray jsonArr = (JSONArray) object;
					for (int i = 0; i < jsonArr.length(); i++) {
						String string2 = jsonArr.getJSONObject(i).toString();
						listMap.add(jsonDTOMap(string2));
					}
					object = listMap;
				}
				if (object != null && !object.equals("null")){
					result.put(key, object);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
}

