package id.co.collega.ifrs.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jet.gand.services.GlobalVariable;
import com.lowagie.text.pdf.codec.Base64;

import id.co.collega.ifrs.master.WndCalculatePDUnconditional;
import id.co.collega.ifrs.master.service.MasterServices;

public class FunctionUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FunctionUtils.class);
	static DTOMap cfg_sys=(DTOMap) GlobalVariable.getInstance().get("cfgsys");
	
	public static String getNumericOnly(String text) {
		String tmp = "";
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) >= '0' && text.charAt(i) <= '9') {
				tmp += text.charAt(i);
			}
		}
		return tmp;
	}
	
	public static Integer getDigitAt(Integer value,Integer valueAtFromRight) {
		int nilai=0;
		try {
			if (valueAtFromRight <= String.valueOf(value).length()) {
				nilai = (int) Math.floor((value / Math.pow(10, valueAtFromRight - 1)) % 10);
				return nilai;
			}else{
				nilai = 1; // value At [valueAtFromRight] yg di request melebihi jumlah value [value]
			}
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return nilai;
	}
	
	public static String moneyToText(BigDecimal money){
		DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance(Locale.getDefault());
		if (money != null) {
			try {
				double nilai = money.doubleValue();
				DecimalFormat df = (DecimalFormat) DecimalFormat.getCurrencyInstance();
				DecimalFormatSymbols dfs = new DecimalFormatSymbols();
				dfs.setCurrencySymbol("");
				dfs.setMonetaryDecimalSeparator('.');
				dfs.setGroupingSeparator(',');
				df.setDecimalFormatSymbols(dfs);
				String value = df.format(nilai);
				
				return value;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 

		return format.format(BigDecimal.ZERO);
	}
	
	public static String doCalculateEngineCutOff(String Id,String userId,String pwd){
        String url =cfg_sys.getString("HOST_ENGINE");
		InputStream inputStream = null;
        String result = "";
        System.out.println("URL="+url);
        
        try {
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);
            String json = "";
            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("id", Id);
            jsonObject.accumulate("userid", userId);
            jsonObject.accumulate("userpass",Base64.encodeBytes(pwd.getBytes()));
            jsonObject.accumulate("arg", "");
            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();
            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content   
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            LOGGER.debug("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }
	
	public static String doCalculateEnginePDUnconditional(String Id,String tglPos,Integer range,Integer horizon,String userId,String pwd){
        String url =cfg_sys.getString("HOST_ENGINE");
		InputStream inputStream = null;
        String result = "";
        System.out.println("URL="+url);
        
        try {
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);
            String json = "";
            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("id", Id);
            jsonObject.accumulate("date", tglPos);
            jsonObject.accumulate("rangeyear", range);
            jsonObject.accumulate("horizon", horizon);
            jsonObject.accumulate("userid", userId);
            jsonObject.accumulate("userpass", Base64.encodeBytes(pwd.getBytes()));
            
            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();
            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content   
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            /*HttpResponse httpResponse = */httpclient.execute(httpPost);

            // 9. receive response as inputStream
            /*inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";*/

        } catch (Exception e) {
            LOGGER.debug("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }
	
	public static String doCalculateEngineLGD(String Id,String tglPos,Integer range,String userId,String pwd){
        String url =cfg_sys.getString("HOST_ENGINE");
		InputStream inputStream = null;
        String result = "";
        System.out.println("URL="+url);
        
        try {
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);
            String json = "";
            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("id", Id);
            jsonObject.accumulate("date", tglPos);
            jsonObject.accumulate("rangeyear", range);
            jsonObject.accumulate("userid", userId);
            jsonObject.accumulate("userpass", Base64.encodeBytes(pwd.getBytes()));
            
            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();
            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content   
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            /*HttpResponse httpResponse = */httpclient.execute(httpPost);

            // 9. receive response as inputStream
            /*inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";*/

        } catch (Exception e) {
            LOGGER.debug("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }
	
	private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    } 

	public static BigDecimal moneyToBigDecimal(String money) {
		if (!money.equals("")) {
			DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance(Locale.getDefault());
			try {
				String value = format.parse(money).toString();
				String a = String.valueOf(value);
				BigDecimal dec = new BigDecimal(a);
				return dec;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} 
		return new BigDecimal("0");
	}
	
	public static BigDecimal moneyToBigDecimal(Double money) {
		if (!money.toString().equals("")) {
			DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance(Locale.getDefault());
			try {
				String value = format.parse(money.toString()).toString();
				String a = String.valueOf(value);
				BigDecimal dec = new BigDecimal(a);
				return dec;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} 
		return new BigDecimal("0");
	}
	
	public static Double moneyToDouble(String money) {
		if (!money.equals("")) {
			String temp = "";
			char c;

			for (int i = 0; i < money.length(); i++) {
				c = money.charAt(i);
				if (c >= '0' && c <= '9' || c == '.')
					temp += c;
			}
			return new Double(temp);
		} else {
			return new Double("0");
		}
	}
	
	public static String toTitleCase(String s) {
		String lowerCase = s.toLowerCase();
		String[] split = lowerCase.split(" ");
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < split.length; i++) {
			char charAt = split[i].charAt(0);
			String upper = String.valueOf(charAt).toUpperCase();
			String substring = upper+split[i].substring(1);
			sb.append(substring+" ");
		}
		
		return sb.toString();
	}
	
	public static String formatNumber(Integer value, String masking, Integer resultLength) {
		if(value != null){
			String result = "";
			
			if (masking == null || masking.equals("")) {
				masking = "0";
			}
			
			for (int i = 0; i < resultLength; i++) {
				result = result + masking;
			}
			
			result = result + value.toString();
			
			return result.substring(result.length() - resultLength, result.length());
		}
		return "";
	}
	
	
	public static String formatNumber(String value, String masking, Integer resultLength) {
		if(value != null){
			String result = "";
			if (masking == null || masking.equals("")) {
				masking = "0";
			}
			for (int i = 0; i < resultLength; i++) {
				result = result + masking;
			}
			result = result + value;
			return result.substring(result.length() - resultLength, result.length());
		}
		return "";
	}
	
	public static String formatNumber(String value, String masking, Integer resultLength, String firstSequenceNo) {
		if(value != null){
			String result = "";
			
			if (masking == null || masking.equals("")) {
				masking = "0";
			}
			
			for (int i = 0; i < resultLength; i++) {
				result = result + masking;
			}
			
			result = value + result;
			
			return result.substring(0, resultLength - 1) + firstSequenceNo;
		}
		return "";
	}
	
	public static String getMoneyIndonesia(BigDecimal money) {
		NumberFormat nf = new DecimalFormat("#,###.##");
		String str = moneyToText(money);
		return nf.format(Long.parseLong(str.trim().replaceAll("\\..+", "").replaceAll(",", ""))).replaceAll("\\..+", "").replaceAll(",", "\\.");
	}
	
	public static String formatNumberLeftAlign(String value, String masking, Integer resultLength) {
		String result = "";
		if (masking == null || masking.equals("")) {
			masking = "0";
		}
		for (int i = 0; i < resultLength; i++) {
			result = masking + result;
		}
		result = value + result;
		return result.substring(0, resultLength);
	}
	
	public static String setDataPrint(DTOMap map){
		String dataPrnt = "";
	    if(map != null){
	    	StringBuffer buf = new StringBuffer();
	        char tanda=28;
	        for (String s : map.map.keySet()) {
	        	if (map.get(s.trim()) != null && !map.get(s.trim()).equals("")) {
					buf.append(s.trim()+":::"+map.get(s.trim())+tanda);
				}else{
					buf.append(s.trim()+"::: "+tanda);
				}
	        }
	        dataPrnt = buf.toString();
	    }
	    return dataPrnt;
	 }
	   
	 public static DTOMap getDataPrint(String data){
		 DTOMap dataprnt = new DTOMap();
		 char tanda = 28;
	     if(data != null){
	    	 String[] dataPrints = data.split(String.valueOf(tanda));
	         if(dataPrints != null){
	        	 for (String s : dataPrints) {
	        		 String[] split = s.split(":::");
	        		 dataprnt.put(split[0], split[1]);
	             }
	         }
	    }
	    return dataprnt;
	}   

	public static String terbilang(double number) {
		String bilangan[] = new String[] { "", "satu ", "dua ", "tiga ", "empat ", "lima ", "enam ", "tujuh ", "delapan ", "sembilan ", "sepuluh ",
		"sebelas " };

		StringBuffer sb = new StringBuffer();
		
		if (number < Double.valueOf(12)) {
			sb.append(bilangan[(int) number]);
		}
		
		if (number >= 12 && number < 20) {
			sb.append(terbilang(number - 10));
			sb.append("belas ");
		}
		
		if (number >= 20 && number < 100) {
			sb.append(terbilang(number / 10));
			sb.append("puluh ");
			sb.append(terbilang(number % 10));
		}
		
		if (number >= 100 && number < 200) {
			sb.append("seratus ");
			sb.append(terbilang(number % 100));
		}
		
		if (number >= 200 && number < 1000) {
			sb.append(terbilang(number / 100));
			sb.append("ratus ");
			sb.append(terbilang(number % 100));
		}
		
		if (number >= 1000 && number < 2000) {
			sb.append("seribu ");
			sb.append(terbilang(number % 1000));
		}
		
		if (number >= 2000 && number < 1000000) {
			sb.append(terbilang(number / 1000));
			sb.append("ribu ");
			sb.append(terbilang(number % 1000));
		}
		
		if (number >= 1000000 && number < 1000000000) {
			sb.append(terbilang(number / 1000000));
			sb.append("juta ");
			sb.append(terbilang(number % 1000000));
		}
		
		if (number >= 1000000000 && number < 1000000000000L) {
			sb.append(terbilang(number / 1000000000));
			sb.append("milyar ");
			sb.append(terbilang(number % 1000000000));
		}
		
		return sb.toString();
	}
	
	public static List<String> getDataFile(BufferedReader br) {
		String record = null;
		try {
			record = new String();
			ArrayList<String> iALWords = new ArrayList<String>();
			while ((record = br.readLine()) != null) {
				System.out.println(record);
				iALWords.add(record);
			}
			
			return iALWords;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new ArrayList<String>();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked", "resource" })
	public static Vector ReadCSV(InputStream is) {
        Vector cellVectorHolder = new Vector();

		//MessageBox.showInformation(fileName+" 2");
        try {
       	 
       	 	POIFSFileSystem myFileSystem = new POIFSFileSystem(is);
               HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
               HSSFSheet mySheet = myWorkBook.getSheetAt(0);
               Iterator rowIter = mySheet.rowIterator();

                while (rowIter.hasNext()) {
                        HSSFRow myRow = (HSSFRow) rowIter.next();
                        Iterator cellIter = myRow.cellIterator();
                        Vector cellStoreVector = new Vector();
                        while (cellIter.hasNext()) {
                                HSSFCell myCell = (HSSFCell) cellIter.next();
                                cellStoreVector.addElement(myCell);
                        }
                        cellVectorHolder.addElement(cellStoreVector);
                }
        } catch (Exception e) {
                e.printStackTrace();
        }
        return cellVectorHolder;
	}
}
