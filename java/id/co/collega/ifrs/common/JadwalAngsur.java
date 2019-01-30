package id.co.collega.ifrs.common;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.jet.gand.services.GlobalVariable;

import id.co.collega.v7.ef.common.DataSession;

/**
 * Creates a window with a JasperReport in it.
 * 
 * @author bbruhns
 * @author sgerth
 * 
 */
 
public class JadwalAngsur extends Window implements Serializable {
	public List listAngsuran;
	public List listAngsuranDiscount;
	public double irr;
	public double irrAtribusi;
	public double irrDiskon;
	public Integer INTDAY;
	public Integer DIVDAY;
	public Integer caraCair=0;
	public Integer stsCIA=0;
	public Listbox listboxJadwalCair = new Listbox();
	public Map dataCair = new HashMap();
	public int jumlahHariTahun = 360;
	public Date dueDt = new Date();
	public DataSession dataSession;
	public DTOMap dtoSys = (DTOMap) GlobalVariable.getInstance().get("cfgsys");

	//		public static double getIRR(double[] cashFlows) {
	//			final int MAX_ITER = 100;
	//			double EXCEL_EPSILON = 0.00000001;
	//	
	//			double x = 0.1;
	//			int iter = 0;
	//			while (iter++ < MAX_ITER) {
	//	
	//				final double x1 = 1.0 + x;
	//				double fx = 0.0;
	//				double dfx = 0.0;
	//				for (int i = 0; i < cashFlows.length; i++) {
	//					final double v = cashFlows[i];
	//					final double x1_i = Math.pow(x1, i);
	//					fx += v / x1_i;
	//					final double x1_i1 = x1_i * x1;
	//					dfx += -i * v / x1_i1;
	//				}
	//				final double new_x = x - fx / dfx;
	//				final double epsilon = Math.abs(new_x - x);
	//	
	//				if (epsilon <= EXCEL_EPSILON) {
	//					if (x == 0.0 && Math.abs(new_x) <= EXCEL_EPSILON) {
	//						return 0.0; // OpenOffice calc does this
	//					} else {
	//						return new_x * 100;
	//					}
	//				}
	//				x = new_x;
	//			}
	//			return x * 100;
	//		}


	// Created By Nurhamid Basith Modified by Jimmi Steven
	public static double getIRR(double[] values) {
		double 	x0 	= 0.0001;			// First Guess
		double	fValue;
		double 	x1;
		boolean	lb_pos2, lb_pos1 = false;

		x1 = x0;
		do{
			fValue = 0;
			for (int k = 0; k < values.length; k++) {		// Start Looping dari Parameter, Create awal start pasti 1
				fValue += values[k] / Math.pow((1.0 + x0),k);
			}

			if(fValue > 0){
				x0 	= x0 + x1;
				lb_pos2 	= true;
			}else{
				x0 	= x0 - x1;
				lb_pos2 	= false;
			}

			if (lb_pos1 != lb_pos2){
				x1 = x1/2;
			}
			lb_pos1 =  lb_pos2;

		}while (Math.abs(fValue) > 0.01);
		return x0 * 100.00;
	}


	//	public static double getIRR(double[] values) {
	//		int maxIterationCount = values.length;
	//		double absoluteAccuracy = 1E-7;
	//
	//		double x0 = 0.1;
	//		double x1 = 0.0;
	//
	//		double fValue = 0;
	//		double fDerivative = 0;
	//		do{
	//			// the value of the function (NPV) and its derivate can be calculated in the same loop
	//			fValue = 0;
	//			fDerivative = 0;
	//			for (int k = 0; k < maxIterationCount; k++) {
	//				fValue += values[k] / Math.pow(1.0 + x0, k);
	//				fDerivative += -k * values[k] / Math.pow(1.0 + x0, k + 1);
	//				//System.out.println("=======VALUES 1======="+fValue);
	//				//System.out.println("=======VALUES 2======="+fDerivative);
	//				//System.out.println("=======VALUES 3======="+values[k]);
	//				//System.out.println("=======VALUES 4======="+k);
	//			}
	//
	//			// the essense of the Newton-Raphson Method
	//			//System.out.println("=======VALUES X0======="+x0);
	//
	//			x1 = x0 - fValue/fDerivative;
	//			//System.out.println("=======VALUES X1======="+x1);
	//
	//			if (Math.abs(x1 - x0) <= absoluteAccuracy) {
	//				return x1*100.00;
	//			}
	//
	//			x0 = x1;
	//		}while (Math.abs(fValue) > 0.01);
	//		// maximum number of iterations is exceeded
	//		return x1*100.00;
	//	}

	public void setCaraCair(Integer caraCair, Listbox listboxJadwalCair){
		this.caraCair = caraCair;
		this.listboxJadwalCair = listboxJadwalCair;
	}
	public void setStatusCIA(Integer stsCIA){
		this.stsCIA = stsCIA;
	}
	public void generateJadwalBertahap(Integer grpPokok){
		if(listboxJadwalCair != null){
			if(listboxJadwalCair.getItemCount() == 0){
				caraCair = 0;
			}
		}
		if(caraCair == 1){
			SimpleDateFormat sdf = new SimpleDateFormat("MMyyyy");
			dataCair = new HashMap();
			for(Listitem item : listboxJadwalCair.getItems()){
				DTOMap data = (DTOMap) item.getAttribute("DATA");
				BigDecimal nominalCair = (BigDecimal)dataCair.get(sdf.format(data.getDate("SCHEDDT")));
				if(nominalCair == null){
					nominalCair=new BigDecimal(0);
				}
				dataCair.put(sdf.format(data.getDate("SCHEDDT")), nominalCair.add(data.getBigDecimal("AMTDISBURS")));
			}
			BigDecimal saldoTeoritis = new BigDecimal(0);
			for (int i = 0; i <grpPokok; i++ ) {
				DTOMap map = (DTOMap) listAngsuran.get(i);
				if(dataCair.get(sdf.format(map.get("tglJadwal")))!= null){
					saldoTeoritis = saldoTeoritis.add((BigDecimal)dataCair.get(sdf.format(map.get("tglJadwal"))));
				}
				map.put("saldoTeoritis", saldoTeoritis);
			}
		}
	}
	//generate angsuran normal
	public List doGenerateAngsuran(Date tglMulai, Integer jangkaWaktu, Integer periodPokok, Integer periodBunga,
			Integer grpPokok, Integer grpBunga, Integer distGrpBunga,
			BigDecimal plafonKredit, BigDecimal provFee, BigDecimal getFee, BigDecimal intRate, BigDecimal discount,
			String tipeBunga, Integer typeIntMod, Integer roundMod, Integer stsRound) {
		listAngsuran=generateAngsuranMaster(tglMulai, tglMulai, jangkaWaktu, periodPokok, periodBunga,
				grpPokok, grpBunga, distGrpBunga,
				plafonKredit, provFee, getFee, intRate, discount,
				tipeBunga, typeIntMod, roundMod, stsRound);
		generateJadwalBertahap(grpPokok);
		listAngsuran=hitungArusKas(jangkaWaktu, listAngsuran, listAngsuranDiscount, plafonKredit, intRate, provFee, getFee,
				roundMod, stsRound, tipeBunga.charAt(0),periodBunga,grpBunga);
		return listAngsuran;
	}

	//generate angsuran normal jika kondisi membutuhkan tanggal buka sebagai acuan tanggal angsur, biasanya dipakai untuk pelunasan sebagian
	public List doGenerateAngsuran(Date tglBuka, Date tglMulai, Integer jangkaWaktu, Integer periodPokok, Integer periodBunga,
			Integer grpPokok, Integer grpBunga, Integer distGrpBunga,
			BigDecimal plafonKredit, BigDecimal provFee, BigDecimal getFee, BigDecimal intRate, BigDecimal discount,
			String tipeBunga, Integer typeIntMod, Integer roundMod, Integer stsRound) {
		listAngsuran=generateAngsuranMaster(tglBuka, tglMulai, jangkaWaktu, periodPokok, periodBunga,
				grpPokok, grpBunga, distGrpBunga,
				plafonKredit, provFee, getFee, intRate, discount,
				tipeBunga, typeIntMod, roundMod, stsRound);
		listAngsuran=hitungArusKas(jangkaWaktu, listAngsuran, listAngsuranDiscount, plafonKredit, intRate, provFee, getFee,
				roundMod, stsRound, tipeBunga.charAt(0),periodBunga,grpBunga);
		return listAngsuran;
	}

	//generate angsuran Tidak Normal/Impairment
	public List doGenerateAngsuranImpairment(Date tglMulai, Integer jangkaWaktu, Integer periodPokok, Integer periodBunga,
			Integer grpPokok, Integer grpBunga, Integer distGrpBunga,
			BigDecimal plafonKredit, BigDecimal provFee, BigDecimal getFee, BigDecimal intRate, BigDecimal discount,
			String tipeBunga, Integer typeIntMod, Integer roundMod, Integer stsRound, BigDecimal irr) {
		listAngsuran=generateAngsuranMaster(tglMulai, tglMulai, jangkaWaktu, periodPokok, periodBunga,
				grpPokok, grpBunga, distGrpBunga,
				plafonKredit, provFee, getFee, intRate, discount,
				tipeBunga, typeIntMod, roundMod, stsRound);
		listAngsuran=hitungArusKasImpairment(jangkaWaktu, listAngsuran, listAngsuranDiscount, plafonKredit, intRate, provFee, getFee,
				roundMod, stsRound, tipeBunga.charAt(0),periodBunga,grpBunga,irr);
		return listAngsuran;
	}
	
	//generate angsuran Tidak Normal/Impairment UPLOAD EXCEL..
	public List doGenerateAngsuranImpairment(Date tglMulai, Integer jangkaWaktu, Integer periodPokok, Integer periodBunga,
				Integer grpPokok, Integer grpBunga, Integer distGrpBunga,
				BigDecimal plafonKredit, BigDecimal provFee, BigDecimal getFee, BigDecimal intRate, BigDecimal discount,
				String tipeBunga, Integer typeIntMod, Integer roundMod, Integer stsRound, BigDecimal irr, List angsurUpload) {		
			listAngsuran=generateAngsuranMaster(tglMulai, tglMulai, jangkaWaktu, periodPokok, periodBunga,
					grpPokok, grpBunga, distGrpBunga,
					plafonKredit, provFee, getFee, intRate, discount,
					tipeBunga, typeIntMod, roundMod, stsRound);		
			listAngsuran=hitungArusKasImpairment(jangkaWaktu, angsurUpload, listAngsuranDiscount, plafonKredit, intRate, provFee, getFee,
					roundMod, stsRound, tipeBunga.charAt(0),periodBunga,grpBunga,irr);
			return listAngsuran;
		}

	//generate angsuran deposito
	public List doGenerateAngsuranDepo(Date tglBuka, Date tglMulai, Integer jangkaWaktu, Integer periodPokok, Integer periodBunga,
			Integer grpPokok, Integer grpBunga, Integer distGrpBunga,
			BigDecimal plafonKredit, BigDecimal provFee, BigDecimal getFee, BigDecimal intRate, BigDecimal discount,
			String tipeBunga, Integer typeIntMod, Integer roundMod, Integer stsRound,Integer intDay,Integer divDay) {
		INTDAY=intDay;
		DIVDAY=divDay;
		listAngsuran=generateAngsuranMaster(tglBuka, tglMulai, jangkaWaktu, periodPokok, periodBunga,
				grpPokok, grpBunga, distGrpBunga,
				plafonKredit, provFee, getFee, intRate, discount,
				tipeBunga, typeIntMod, roundMod, stsRound);
		listAngsuran=hitungArusKas(jangkaWaktu, listAngsuran, listAngsuranDiscount, plafonKredit, intRate, provFee, getFee,
				roundMod, stsRound, tipeBunga.charAt(0),periodBunga,grpBunga);
		return listAngsuran;
	}

	public List generateAngsuranMaster(Date tglBuka, Date tglMulai, Integer jangkaWaktu, Integer periodPokok, Integer periodBunga,
			Integer grpPokok, Integer grpBunga, Integer distGrpBunga,
			BigDecimal plafonKredit, BigDecimal provFee, BigDecimal getFee, BigDecimal intRate, BigDecimal discount,
			String tipeBunga, Integer typeIntMod, Integer roundMod, Integer stsRound) {
		Calendar tglJadwal = Calendar.getInstance();
		tglJadwal.setTime(tglMulai);
		/*if(DataSession.getSysMap().getString("BANKID").equals("132")) { // PAPUA
			tglJadwal.set(Calendar.DATE, tglBuka.getDate());
		}*/
		int date = tglBuka.getDate();
		listAngsuran = new ArrayList();
		listAngsuranDiscount = new ArrayList();
		DTOMap map = new DTOMap();
		map.put("jadwalAngsurKe", 0);
		map.put("tglJadwal", tglJadwal.getTime());
		map.put("noArus", map.get("jadwalAngsurKe"));
		map.put("tglArus", map.get("tglJadwal"));
		map.put("angsuranPokok", new BigDecimal(0));
		map.put("angsuranBunga", new BigDecimal(0));
		map.put("totalAngsuran", new BigDecimal(0));
		map.put("saldoTeoritis", plafonKredit);
		listAngsuran.add(map);
		if(stsCIA != 1 && !tipeBunga.equals("I")){
			tglJadwal.add(Calendar.MONTH, 1);
			if(date > tglJadwal.getTime().getDate()){
				tglJadwal.set(Calendar.DATE, tglJadwal.getActualMaximum(Calendar.DATE));
				if(tglJadwal.getTime().getDate() > date){
					tglJadwal.set(Calendar.DATE, date);
				}
			}
		}
		for (int baris=1;baris<=jangkaWaktu;baris+=1) {
			map = new DTOMap();
			map.put("jadwalAngsurKe", baris);
			map.put("tglJadwal", tglJadwal.getTime());
			map.put("noArus", map.get("jadwalAngsurKe"));
			map.put("tglArus", map.get("tglJadwal"));
			map.put("angsuranPokok", new BigDecimal(0));
			map.put("angsuranBunga", new BigDecimal(0));
			map.put("totalAngsuran", new BigDecimal(0));
			map.put("saldoTeoritis", plafonKredit);
			listAngsuran.add(map);
			tglJadwal.add(Calendar.MONTH, 1);
			if(date > tglJadwal.getTime().getDate()){
				tglJadwal.set(Calendar.DATE, tglJadwal.getActualMaximum(Calendar.DATE));
				if(tglJadwal.getTime().getDate() > date){
					tglJadwal.set(Calendar.DATE, date);
				}
			}
		}
		if(discount.doubleValue()>0){
			Calendar tglJadwalDiskon = Calendar.getInstance();
			tglJadwalDiskon.setTime(tglMulai);
			
			map = new DTOMap();
			map.put("jadwalAngsurKe", 0);
			map.put("tglJadwal", tglJadwalDiskon.getTime());
			map.put("noArus", map.get("jadwalAngsurKe"));
			map.put("tglArus", map.get("tglJadwal"));
			map.put("angsuranPokok", new BigDecimal(0));
			map.put("angsuranBunga", new BigDecimal(0));
			map.put("totalAngsuran", new BigDecimal(0));
			map.put("saldoTeoritis", plafonKredit);
			listAngsuranDiscount.add(map);
			tglJadwalDiskon.add(Calendar.MONTH, 1);
			if(date > tglJadwalDiskon.getTime().getDate()){
				tglJadwalDiskon.set(Calendar.DATE, tglJadwalDiskon.getActualMaximum(Calendar.DATE));
				if(tglJadwalDiskon.getTime().getDate() > date){
					tglJadwalDiskon.set(Calendar.DATE, date);
				}
			}
			for (int baris=1;baris<=jangkaWaktu;baris+=1) {
				map = new DTOMap();
				map.put("jadwalAngsurKe", baris);
				map.put("tglJadwal", tglJadwalDiskon.getTime());
				map.put("noArus", map.get("jadwalAngsurKe"));
				map.put("tglArus", map.get("tglJadwal"));
				map.put("angsuranPokok", new BigDecimal(0));
				map.put("angsuranBunga", new BigDecimal(0));
				map.put("totalAngsuran", new BigDecimal(0));
				map.put("saldoTeoritis", plafonKredit);
				listAngsuranDiscount.add(map);
				tglJadwalDiskon.add(Calendar.MONTH, 1);
				if(date > tglJadwalDiskon.getTime().getDate()){
					tglJadwalDiskon.set(Calendar.DATE, tglJadwalDiskon.getActualMaximum(Calendar.DATE));
					if(tglJadwalDiskon.getTime().getDate() > date){
						tglJadwalDiskon.set(Calendar.DATE, date);
					}
				}
			}
		}

		switch (typeIntMod) {
		case 1:
			//Model ACEH
			switch (tipeBunga.charAt(0)) {
			case 'A':
				//ANUITAS BULANAN
				listAngsuran=anuitasBulananSumut(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=anuitasBulananSumut(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			case 'B':
				//ANUITAS TAHUNAN
				listAngsuran=anuitasTahunanSumut(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=anuitasTahunanSumut(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			case 'C':
				//FLAT
				listAngsuran=flatSumut(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=flatSumut(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			case 'D':
				//FLAT TO ANUITAS
				listAngsuran=flatToAnuitas(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=flatToAnuitas(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			case 'E':
				//Rata-Rata
				listAngsuran=rataRata(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=rataRata(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			case 'F':
			case 'G':
			case 'H':
			case 'L':
				//SLIDING SALDO TEORI
				listAngsuran=slidingSaldoTeori(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						tipeBunga.charAt(0), roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=slidingSaldoTeori(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							tipeBunga.charAt(0), roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			case 'K':
				listAngsuran=angsuranBungaEfektif(listAngsuran, plafonKredit, roundMod, stsRound);
				break;
			case 'Z':
				listAngsuran=angsuranDeposito(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga,INTDAY,DIVDAY);
				break;
			case 'I' :
				//ANUITAS IN ADVANCE
				listAngsuran=anuitasInAdvance(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=anuitasInAdvance(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			default:
				break;
			}
			break;
		case 2:
			//Model NAGARI
			switch (tipeBunga.charAt(0)) {
			case 'A':
				//ANUITAS BULANAN
				listAngsuran=anuitasBulananSumut(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=anuitasBulananSumut(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			case 'B':
				//ANUITAS TAHUNAN
				listAngsuran=anuitasTahunanSumut(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=anuitasTahunanSumut(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			case 'C':
				//FLAT
				listAngsuran=flatSumut(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=flatSumut(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			case 'D':
				//FLAT TO ANUITAS
				listAngsuran=flatToAnuitas(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=flatToAnuitas(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			case 'E':
				//Rata-Rata
				listAngsuran=rataRata(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=rataRata(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			case 'F':
			case 'G':
			case 'H':
			case 'L':
				//SLIDING SALDO TEORI
				listAngsuran=slidingSaldoTeori(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						tipeBunga.charAt(0), roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=slidingSaldoTeori(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							tipeBunga.charAt(0), roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			case 'K':
				listAngsuran=angsuranBungaEfektif(listAngsuran, plafonKredit, roundMod, stsRound);
				break;
			case 'Z':
				listAngsuran=angsuranDeposito(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga,INTDAY,DIVDAY);
				break;
			case 'I' :
				//ANUITAS IN ADVANCE
				listAngsuran=anuitasInAdvance(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=anuitasInAdvance(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			default:
				break;
			}
			break;
		case 3:
			//Model NTT
			switch (tipeBunga.charAt(0)) {
			case 'A':
				//ANUITAS BULANAN
				listAngsuran=anuitasBulananSumut(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=anuitasBulananSumut(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			case 'B':
				//ANUITAS TAHUNAN
				listAngsuran=anuitasTahunanSumut(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=anuitasTahunanSumut(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			case 'C':
				//FLAT
				listAngsuran=flatSumut(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=flatSumut(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			case 'D':
				//FLAT TO ANUITAS
				listAngsuran=flatToAnuitas(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=flatToAnuitas(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			case 'E':
				//Rata-Rata
				listAngsuran=rataRata(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=rataRata(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			case 'F':
			case 'G':
			case 'H':
			case 'L':
				//SLIDING SALDO TEORI
				listAngsuran=slidingSaldoTeori(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						tipeBunga.charAt(0), roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=slidingSaldoTeori(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							tipeBunga.charAt(0), roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			case 'K':
				listAngsuran=angsuranBungaEfektif(listAngsuran, plafonKredit, roundMod, stsRound);
				break;
			case 'Z':
				listAngsuran=angsuranDeposito(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga,INTDAY,DIVDAY);
				break;
			case 'I' :
				//ANUITAS IN ADVANCE
				listAngsuran=anuitasInAdvance(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=anuitasInAdvance(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			default:
				break;
			}
			break;
		case 4:
			//Model SUMUT
			switch (tipeBunga.charAt(0)) {
			case 'A':
				//ANUITAS BULANAN
				listAngsuran=anuitasBulananSumut(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=anuitasBulananSumut(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			case 'B':
				//ANUITAS TAHUNAN
				listAngsuran=anuitasTahunanSumut(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=anuitasTahunanSumut(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			case 'C':
				//FLAT
				listAngsuran=flatSumut(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=flatSumut(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			case 'D':
				//FLAT TO ANUITAS
				listAngsuran=flatToAnuitas(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=flatToAnuitas(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			case 'E':
				//Rata-Rata
				listAngsuran=rataRata(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=rataRata(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			case 'F':
			case 'G':
			case 'H':
			case 'L':
				//SLIDING SALDO TEORI
				listAngsuran=slidingSaldoTeori(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						tipeBunga.charAt(0), roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=slidingSaldoTeori(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							tipeBunga.charAt(0), roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			case 'K':
				listAngsuran=angsuranBungaEfektif(listAngsuran, plafonKredit, roundMod, stsRound);
				break;
			case 'Z':
				listAngsuran=angsuranDeposito(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga,INTDAY,DIVDAY);
				break;
			case 'I' :
				//ANUITAS IN ADVANCE
				listAngsuran=anuitasInAdvance(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=anuitasInAdvance(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			default:
				break;
			}
			break;
		case 5:
			//Model BENGKULU
			switch (tipeBunga.charAt(0)) {
			case 'A':
				//ANUITAS BULANAN
				listAngsuran=anuitasBulananSumut(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=anuitasBulananSumut(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			case 'B':
				//ANUITAS TAHUNAN
				listAngsuran=anuitasTahunanSumut(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=anuitasTahunanSumut(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			case 'C':
				//FLAT
				listAngsuran=flatSumut(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=flatSumut(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			case 'D':
				//FLAT TO ANUITAS
				listAngsuran=flatToAnuitas(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=flatToAnuitas(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			case 'E':
				//Rata-Rata
				listAngsuran=rataRata(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=rataRata(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			case 'F':
			case 'G':
			case 'H':
			case 'L':
				//SLIDING SALDO TEORI
				listAngsuran=slidingSaldoTeori(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						tipeBunga.charAt(0), roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=slidingSaldoTeori(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							tipeBunga.charAt(0), roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			case 'K':
				listAngsuran=angsuranBungaEfektif(listAngsuran, plafonKredit, roundMod, stsRound);
				break;
			case 'Z':
				listAngsuran=angsuranDeposito(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga,INTDAY,DIVDAY);
				break;
			case 'I' :
				//ANUITAS IN ADVANCE
				listAngsuran=anuitasInAdvance(jangkaWaktu, listAngsuran, plafonKredit, intRate, 
						roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				if(discount.doubleValue()>0)
					listAngsuranDiscount=anuitasInAdvance(jangkaWaktu, listAngsuranDiscount, plafonKredit, intRate.add(discount), 
							roundMod, stsRound, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
				break;
			default:
				break;
			}
			break;

		default:
			break;
		}

		return listAngsuran;
	}
	
	public List anuitasBulananSumut(Integer jangkaWaktu, List listAngsuran, BigDecimal maxCR, BigDecimal intRate, 
			Integer roundmod, Integer stsround, Integer periodPokok, Integer periodBunga, Integer grpPokok, Integer grpBunga, Integer distGrpBunga) {
		int distCounter=distGrpBunga;
		double intRateD = (intRate.doubleValue()/(12/1))/100;
		double saldoTeoritis=maxCR.doubleValue();

		double totalAngsuran=0;
		if(intRate.doubleValue()>0)
			totalAngsuran=maxCR.doubleValue()*((intRate.doubleValue()/(12/periodPokok))/100)/(1-(1/(Math.pow(1 + ((intRate.doubleValue()/(12/periodPokok))/100), (jangkaWaktu-grpPokok)/periodPokok))));
		else
			totalAngsuran=maxCR.doubleValue()/jangkaWaktu;
		totalAngsuran=rounding(totalAngsuran, roundmod, stsround);
		double bungaTemp=0;
		for (int i = 1; i <listAngsuran.size(); i++) {
			DTOMap angsur = (DTOMap) listAngsuran.get(i);
			double bunga=0,pokok=0;
			bunga=saldoTeoritis*intRateD*(i%periodBunga==0?periodBunga:0);
			bunga=rounding(bunga, roundmod, stsround);
			if(i!=listAngsuran.size()-1){
				bungaTemp+=bunga;
			}
			if (i>grpPokok){
				pokok=(totalAngsuran-bungaTemp)*(i%periodPokok==0?1:0);
				pokok=rounding(pokok, roundmod, stsround);
			}

			if(i==listAngsuran.size()-1){
				pokok=saldoTeoritis;
				bunga=totalAngsuran-pokok-bungaTemp;
			}

			if(i%periodPokok==0){
				bungaTemp=0;
			}


			saldoTeoritis-=pokok;
			saldoTeoritis=rounding(saldoTeoritis, roundmod, stsround);

			angsur.put("angsuranPokok", BigDecimal.valueOf(pokok));
			angsur.put("angsuranBunga", BigDecimal.valueOf(bunga));
			angsur.put("totalAngsuran", BigDecimal.valueOf(pokok+bunga));
			angsur.put("saldoTeoritis", BigDecimal.valueOf(saldoTeoritis));
		}

		if(grpBunga>0){
			double totalGrpBunga=0;
			for (int i = 1; i <= grpBunga; i++ ) {
				DTOMap angsur = (DTOMap) listAngsuran.get(i);
				totalGrpBunga+=angsur.getBigDecimal("angsuranBunga").doubleValue();
				angsur.put("totalAngsuran", angsur.getBigDecimal("totalAngsuran").subtract(angsur.getBigDecimal("angsuranBunga")));
				angsur.put("angsuranBunga", BigDecimal.ZERO);
			}
			if(distGrpBunga>0){
				double distBungaValue=rounding(totalGrpBunga/distGrpBunga, roundmod, stsround);
				for (int i = grpBunga+1; i <=grpBunga+distGrpBunga; i++) {
					DTOMap angsur = (DTOMap) listAngsuran.get(i);
					double bunga=rounding(angsur.getBigDecimal("angsuranBunga").doubleValue()+distBungaValue, roundmod, stsround);
					double totalAngsur=angsur.getBigDecimal("angsuranPokok").doubleValue()+bunga;
					angsur.put("angsuranBunga", new BigDecimal(bunga));
					angsur.put("totalAngsuran", new BigDecimal(totalAngsur));
				}
			}
		}

		return listAngsuran;
	}

	public List anuitasTahunanSumut(Integer jangkaWaktu, List listAngsuran, BigDecimal maxCR, BigDecimal intRate, 
			Integer roundmod, Integer stsround, Integer periodPokok, Integer periodBunga, Integer grpPokok, Integer grpBunga, Integer distGrpBunga) {
		if(intRate.doubleValue() == 0){
			throw new WrongValueException("Untuk Jenis Bunga Anuitas Tahunan persen Bunga Harus Lebih Besar Dari 0.\n Harap Lakukan Perbaikan Parameter Produk.");
		}
		int distCounter=distGrpBunga;
		double intRateD = intRate.doubleValue()/100.00;
		double saldoTeoritis=maxCR.doubleValue();
		double bakiDebetAwalTahun=maxCR.doubleValue();

		double totalAngsuran=maxCR.doubleValue()*intRateD/(1-(1/(Math.pow(1 + intRateD, (jangkaWaktu-grpPokok)/12.0))))/12.0;
		totalAngsuran=rounding(totalAngsuran, roundmod, stsround);

		double accumBunga=0;
		double accumPokok=0;
		for (int i = 1; i <listAngsuran.size(); i++ ) {
			DTOMap angsur = (DTOMap) listAngsuran.get(i);
			double bunga=0,pokok=0,bungaPut=0;


			bunga=bakiDebetAwalTahun*(intRate.doubleValue()/(1200.00/(i%periodBunga==0?periodBunga:0)));
			bunga=rounding(bunga, roundmod, stsround);
			if (i>grpBunga){
				if(distCounter>0){
					bunga+=(bunga*grpBunga)/distGrpBunga;
					distCounter--;
				}
				bungaPut=accumBunga+bunga;
				accumBunga=0;
			}

			if (i>grpPokok){
				pokok=totalAngsuran-bunga;
				pokok+=accumPokok;
				accumPokok=0;
			}

			if(i==listAngsuran.size()-1){
				pokok=saldoTeoritis;
				bungaPut=totalAngsuran-pokok;
			}

			saldoTeoritis-=pokok;
			saldoTeoritis=rounding(saldoTeoritis, roundmod, stsround);

			angsur.put("angsuranPokok", BigDecimal.valueOf(pokok));
			angsur.put("angsuranBunga", BigDecimal.valueOf(bungaPut));
			angsur.put("totalAngsuran", BigDecimal.valueOf(pokok+bungaPut));
			angsur.put("saldoTeoritis", BigDecimal.valueOf(saldoTeoritis));

			if(i%12==0){
				bakiDebetAwalTahun=saldoTeoritis;
			}
		}

		return listAngsuran;
	}
	
	public List anuitasInAdvance(Integer jangkaWaktu, List listAngsuran, BigDecimal maxCR, BigDecimal intRate, 
			Integer roundmod, Integer stsround, Integer periodPokok, Integer periodBunga, Integer grpPokok, Integer grpBunga, Integer distGrpBunga) {
		int distCounter=distGrpBunga;
		double intRateD = (intRate.doubleValue()/(12/periodPokok))/100;
		double saldoTeoritis=maxCR.doubleValue();

		double totalAngsuran=0;
		if(intRate.doubleValue()>0){
			if(stsCIA == 1){
				totalAngsuran=maxCR.doubleValue()*intRateD/(1-(1/(Math.pow(1 + intRateD, (jangkaWaktu-grpPokok)/periodPokok))));
			}else{
				totalAngsuran=maxCR.doubleValue()*intRateD/((1-(1/(Math.pow(1 + intRateD, (jangkaWaktu-grpPokok)/periodPokok))))*(1+intRateD));
			}
		}else{
			totalAngsuran=maxCR.doubleValue()/jangkaWaktu;
		}
		totalAngsuran=rounding(totalAngsuran, roundmod, stsround);
		double bungaTemp=0;
		for (int i = 1; i <listAngsuran.size(); i++) {
			DTOMap angsur = (DTOMap) listAngsuran.get(i);
			double bunga=0,pokok=0;
			if(i > 1 || stsCIA == 1){
				bunga=saldoTeoritis*intRateD*(i%periodBunga==0?periodBunga:0);
				bunga=rounding(bunga, roundmod, stsround);
			}
			if(i!=listAngsuran.size()-1){
				bungaTemp+=bunga;
			}
			if (i>grpPokok){
				pokok=(totalAngsuran-bungaTemp)*(i%periodPokok==0?1:0);
				pokok=rounding(pokok, roundmod, stsround);
			}
			if(i==listAngsuran.size()-1){
				pokok=saldoTeoritis;
				bunga=totalAngsuran-pokok-bungaTemp;
			}

			if(i%periodPokok==0){
				bungaTemp=0;
			}


			saldoTeoritis-=pokok;
			saldoTeoritis=rounding(saldoTeoritis, roundmod, stsround);

			angsur.put("angsuranPokok", BigDecimal.valueOf(pokok));
			angsur.put("angsuranBunga", BigDecimal.valueOf(bunga));
			angsur.put("totalAngsuran", BigDecimal.valueOf(pokok+bunga));
			angsur.put("saldoTeoritis", BigDecimal.valueOf(saldoTeoritis));
		}

		if(grpBunga>0){
			double totalGrpBunga=0;
			for (int i = 1; i <= grpBunga; i++ ) {
				DTOMap angsur = (DTOMap) listAngsuran.get(i);
				totalGrpBunga+=angsur.getBigDecimal("angsuranBunga").doubleValue();
				angsur.put("totalAngsuran", angsur.getBigDecimal("totalAngsuran").subtract(angsur.getBigDecimal("angsuranBunga")));
				angsur.put("angsuranBunga", BigDecimal.ZERO);
			}
			if(distGrpBunga>0){
				double distBungaValue=rounding(totalGrpBunga/distGrpBunga, roundmod, stsround);
				for (int i = grpBunga+1; i <=grpBunga+distGrpBunga; i++) {
					DTOMap angsur = (DTOMap) listAngsuran.get(i);
					double bunga=rounding(angsur.getBigDecimal("angsuranBunga").doubleValue()+distBungaValue, roundmod, stsround);
					double totalAngsur=angsur.getBigDecimal("angsuranPokok").doubleValue()+bunga;
					angsur.put("angsuranBunga", new BigDecimal(bunga));
					angsur.put("totalAngsuran", new BigDecimal(totalAngsur));
				}
			}
		}

		return listAngsuran;
	}

	public List flatSumut(Integer jangkaWaktu, List listAngsuran, BigDecimal maxCR, BigDecimal intRate, Integer roundmod, Integer stsround,
			Integer periodPokok, Integer periodBunga, Integer grpPokok, Integer grpBunga, Integer distGrpBunga) {
		double pokokSingle=(maxCR.doubleValue()/Double.valueOf(jangkaWaktu-grpPokok))*periodPokok;
		pokokSingle=rounding(pokokSingle,roundmod,stsround);
		double bungaSingle=(maxCR.doubleValue()*intRate.doubleValue())/(1200.00/1);
		bungaSingle=rounding(bungaSingle,roundmod,stsround);
		double saldoTeoritis=maxCR.doubleValue();

		double distBunga=0;
		if(distGrpBunga>0){
			distBunga=bungaSingle*grpBunga/distGrpBunga;
			distBunga=rounding(distBunga,roundmod,stsround);
		}

		for (int i=1; i<listAngsuran.size(); i++) {
			DTOMap angsur=(DTOMap)listAngsuran.get(i);
			double bunga=0,pokok=0,totalAngsuran=0;

			if(i>grpBunga){
				if(distGrpBunga>0){
					bunga+=distBunga;
					distGrpBunga--;
				}
				bunga+=(bungaSingle * (i%periodBunga==0?periodBunga:0));
			}

			if(i>grpPokok && angsur.getInt("jadwalAngsurKe")%periodPokok==0){
				pokok=pokokSingle;
			}

			if(i==jangkaWaktu){
				pokok=saldoTeoritis;
			}

			totalAngsuran=bunga+pokok;
			totalAngsuran=rounding(totalAngsuran,roundmod,stsround);

			saldoTeoritis=saldoTeoritis-pokok;
			saldoTeoritis=rounding(saldoTeoritis,roundmod,stsround);
			angsur.put("angsuranPokok", BigDecimal.valueOf(pokok));
			angsur.put("angsuranBunga", BigDecimal.valueOf(bunga));
			angsur.put("totalAngsuran", BigDecimal.valueOf(totalAngsuran));
			angsur.put("saldoTeoritis", BigDecimal.valueOf(saldoTeoritis));
		}
		return listAngsuran;
	}

	public List angsuranDeposito(Integer jangkaWaktu, List listAngsuran, BigDecimal maxCR, BigDecimal intRate, Integer roundmod, Integer stsround,
			Integer periodPokok, Integer periodBunga, Integer grpPokok, Integer grpBunga, Integer distGrpBunga,Integer INTDAY,Integer DIVDAY) {
		double pokokSingle=(maxCR.doubleValue()/Double.valueOf(jangkaWaktu-grpPokok))*periodPokok;
		pokokSingle=rounding(pokokSingle,roundmod,stsround);
		double bungaSingle=(maxCR.doubleValue()*intRate.doubleValue())/(1200.00/1);
		bungaSingle=rounding(bungaSingle,roundmod,stsround);
		double saldoTeoritis=maxCR.doubleValue();

		double distBunga=0;
		double hariBunga=0;
		if(distGrpBunga>0){
			distBunga=bungaSingle*grpBunga/distGrpBunga;
			distBunga=rounding(distBunga,roundmod,stsround);
		}

		for (int i=1; i<listAngsuran.size(); i++) {
			DTOMap angsur=(DTOMap)listAngsuran.get(i);
			double bunga=0,pokok=0,totalAngsuran=0;

			if(i>grpPokok && angsur.getInt("jadwalAngsurKe")%periodPokok==0){
				pokok=pokokSingle;
			}

			if(i==jangkaWaktu){
				pokok=saldoTeoritis;
			}

			hariBunga= getHariBunga(angsur.getDate("tglJadwal"),INTDAY);
			bunga = rounding((saldoTeoritis* (intRate.doubleValue()/100)* (hariBunga/DIVDAY)),roundmod,stsround);

			totalAngsuran=bunga+pokok;
			totalAngsuran=rounding(totalAngsuran,roundmod,stsround);

			saldoTeoritis=saldoTeoritis-pokok;
			saldoTeoritis=rounding(saldoTeoritis,roundmod,stsround);
			angsur.put("angsuranPokok", BigDecimal.valueOf(pokok));
			angsur.put("angsuranBunga", BigDecimal.valueOf(bunga));
			angsur.put("totalAngsuran", BigDecimal.valueOf(totalAngsuran));
			angsur.put("saldoTeoritis", BigDecimal.valueOf(saldoTeoritis));
		}

		return listAngsuran;
	}

	public Integer getHariBunga(Date tglJadwal, Integer intDay) {
		Integer hariBunga=0;
		if (intDay==360) {
			hariBunga=30;
		}else{
			Calendar cld=Calendar.getInstance();
			cld.setTime(tglJadwal);
			int bulan=0;
			if (cld.get(Calendar.MONTH)<1) {
				bulan = 12;
			}else {
				bulan=cld.get(Calendar.MONTH);
			}

			switch (bulan) {
			case 1:
				hariBunga=31;
				break;
			case 2:
				if (intDay==365) {
					hariBunga=28;
				}else{
					if (cld.get(Calendar.YEAR) % 4==0) {
						hariBunga=29;
					}else {
						hariBunga=28;
					}
				}
				break;
			case 3:
				hariBunga=31;
				break;
			case 4:
				hariBunga=30;
				break;
			case 5:
				hariBunga=31;
				break;
			case 6:
				hariBunga=30;
				break;
			case 7:
				hariBunga=31;
				break;
			case 8:
				hariBunga=31;
				break;
			case 9:
				hariBunga=30;
				break;
			case 10:
				hariBunga=31;
				break;
			case 11:
				hariBunga=30;
				break;
			case 12:
				hariBunga=31;
			default:
				break;
			}
		}
		return hariBunga;
	}

	public List slidingBulanan(Integer jangkaWaktu, List listAngsuran, BigDecimal maxCR, BigDecimal intRate, 
			Integer roundmod, Integer stsround, Integer periodPokok, Integer periodBunga, Integer grpPokok, Integer grpBunga){
		double plafond=maxCR.doubleValue();
		double bakiDebet=maxCR.doubleValue();
		double decPersenBunga=intRate.doubleValue()/100.0;
		for (int i = 1; i<listAngsuran.size(); i++) {
			double bunga=bakiDebet * decPersenBunga / 12;
			bunga=rounding(bunga,roundmod,stsround);
			double pokok=(plafond / (jangkaWaktu - grpPokok));
			pokok=rounding(pokok,roundmod,stsround);
			double totalAngsuran=bunga+pokok;
			totalAngsuran=rounding(totalAngsuran,roundmod,stsround);

			bakiDebet-=pokok;
			bakiDebet=rounding(bakiDebet,roundmod,stsround);
			DTOMap angsur=(DTOMap)listAngsuran.get(i);
			angsur.put("angsuranPokok", BigDecimal.valueOf(pokok));
			angsur.put("angsuranBunga", BigDecimal.valueOf(bunga));
			angsur.put("totalAngsuran", BigDecimal.valueOf(totalAngsuran));
			angsur.put("saldoTeoritis", BigDecimal.valueOf(bakiDebet));
		}

		return listAngsuran;
	}

	public List flatToAnuitas(Integer jangkaWaktu, List listAngsuran, BigDecimal maxCR, BigDecimal intRate, 
			Integer roundmod, Integer stsround, Integer periodPokok, Integer periodBunga, Integer grpPokok, Integer grpBunga, Integer distGrpBunga){
		listAngsuran=flatSumut(jangkaWaktu, listAngsuran, maxCR, intRate, roundmod, stsround, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);
		double[] cashFlow=new double[jangkaWaktu.intValue()+1];
		cashFlow[0]=-maxCR.doubleValue();
		int count = 1;
		for (int i=1; i<listAngsuran.size(); i++) {
			DTOMap angsur = (DTOMap) listAngsuran.get(i);
			//if(angsur.getBigDecimal("totalAngsuran").doubleValue() != 0){
			cashFlow[count]=angsur.getBigDecimal("totalAngsuran").doubleValue();
			count++;
			//}
		}
		double getIrr = getIRR(cashFlow);
		if((getIrr+"").equals("NaN")){
			throw new WrongValueException("flat to anuitas. Tidak Bisa Ambil IRR. \n maximum number of iterations is exceeded.");
		}
		double irr=getIrr*(12/periodPokok);
		listAngsuran=anuitasBulananSumut(jangkaWaktu, listAngsuran, maxCR, new BigDecimal(irr), roundmod, stsround, periodPokok, periodBunga, grpPokok, grpBunga, distGrpBunga);

		return listAngsuran;
	}

	public List rataRata(Integer jangkaWaktu, List listAngsuran, BigDecimal maxCR, BigDecimal intRate, 
			Integer roundmod, Integer stsround, Integer periodPokok, Integer periodBunga, Integer grpPokok, Integer grpBunga, 
			Integer distGrpBunga) {
		double pokokSingle=(maxCR.doubleValue()/Double.valueOf(jangkaWaktu-grpPokok))*periodPokok;
		pokokSingle=rounding(pokokSingle,roundmod,stsround);
		double bungaSingle=(Double.valueOf((jangkaWaktu-grpPokok)+1))/(Double.valueOf(2*(jangkaWaktu-grpPokok)))*maxCR.doubleValue()*(intRate.doubleValue()/(1200/1));
		bungaSingle=rounding(bungaSingle,roundmod,stsround);
		double saldoTeoritis=maxCR.doubleValue();

		double distBunga=0;
		if(distGrpBunga>0){
			distBunga=(bungaSingle*grpBunga)/distGrpBunga;
		}

		for (int i = 1; i<listAngsuran.size(); i++) {
			double bunga=0,pokok=0,totalAngsuran=0;
			DTOMap angsur=(DTOMap)listAngsuran.get(i);

			if(i>grpBunga){
				if(distGrpBunga>0){
					bunga=(bungaSingle+distBunga)*(i%periodBunga==0?periodBunga:0);
					distGrpBunga--;
				} else
					bunga=bungaSingle*(i%periodBunga==0?periodBunga:0);
			}

			if(i>grpPokok && angsur.getInt("jadwalAngsurKe")%periodPokok==0){
				pokok=pokokSingle;
			}

			if(i==jangkaWaktu){
				pokok=saldoTeoritis;
			}

			totalAngsuran=bunga+pokok;
			totalAngsuran=rounding(totalAngsuran,roundmod,stsround);

			saldoTeoritis=saldoTeoritis-pokok;
			saldoTeoritis=rounding(saldoTeoritis,roundmod,stsround);
			angsur.put("angsuranPokok", BigDecimal.valueOf(pokok));
			angsur.put("angsuranBunga", BigDecimal.valueOf(bunga));
			angsur.put("totalAngsuran", BigDecimal.valueOf(totalAngsuran));
			angsur.put("saldoTeoritis", BigDecimal.valueOf(saldoTeoritis));
		}

		return listAngsuran;
	}

	public List slidingSaldoTeori(Integer jangkaWaktu, List listAngsuran, BigDecimal maxCR, BigDecimal intRate, char intType, Integer roundmod, Integer stsround,
			Integer periodPokok, Integer periodBunga, Integer grpPokok, Integer grpBunga, Integer distGrpBunga) {
		double pokokSingle=(maxCR.doubleValue()/Double.valueOf(jangkaWaktu-grpPokok))*periodPokok;
		pokokSingle=rounding(pokokSingle,roundmod,stsround);
		double saldoTeoritis=maxCR.doubleValue();

		double distBunga=0;
		Calendar cldOpenDate = Calendar.getInstance();
		cldOpenDate.setTime(dtoSys.getDate("OPEN_DATE"));
		int maxHariBulan = cldOpenDate.getActualMaximum(Calendar.DATE);

		Calendar cldLastDayOfYear = Calendar.getInstance();
		cldLastDayOfYear.setTime(dtoSys.getDate("OPEN_DATE"));
		cldLastDayOfYear.set(Calendar.DATE, 31);
		cldLastDayOfYear.set(Calendar.MONTH, 11);
		int jumlahHariTahunIrr = cldLastDayOfYear.get(Calendar.DAY_OF_YEAR);
		if(distGrpBunga>0){
			if(intType=='F' || intType=='L'){
				distBunga=(maxCR.doubleValue()*intRate.doubleValue()*grpBunga*1) * maxHariBulan/((jumlahHariTahunIrr * 100.00)*Double.valueOf(jangkaWaktu-grpBunga));
			}else{
				distBunga=(maxCR.doubleValue()*intRate.doubleValue()*grpBunga*1)/(1200.00*Double.valueOf(jangkaWaktu-grpBunga));
			}
			distBunga=rounding(distBunga,roundmod,stsround);
		}

		double accumBunga=0;
		for (int i = 1; i<listAngsuran.size(); i++) {
			double bunga=0,pokok=0,totalAngsuran=0;
			DTOMap angsur=(DTOMap)listAngsuran.get(i);

			if(i>grpBunga){
				if(intType=='F' || intType=='L'){
					DTOMap angsurBefore=(DTOMap)listAngsuran.get(i-1);
					Calendar cldtgl = Calendar.getInstance();
					cldtgl.setTime(angsurBefore.getDate("tglJadwal"));
					maxHariBulan = cldtgl.getActualMaximum(Calendar.DATE);
					bunga=saldoTeoritis*(intRate.doubleValue() * maxHariBulan/((jumlahHariTahun * 100.0)/(i%periodBunga==0?periodBunga:0)));
				}else{
					bunga=saldoTeoritis*(intRate.doubleValue()/(1200.0/(i%periodBunga==0?periodBunga:0)));
				}
				bunga = rounding(bunga,roundmod,stsround);

				if(distGrpBunga>0){
					bunga+=distBunga;
					distGrpBunga--;
				}
				bunga+=accumBunga;
				accumBunga=0;
			}

			if(i>grpPokok && angsur.getInt("jadwalAngsurKe")%periodPokok==0){
				pokok=pokokSingle;
			}

			if(i==jangkaWaktu){
				pokok=saldoTeoritis;
			}

			totalAngsuran=bunga+pokok;
			totalAngsuran=rounding(totalAngsuran,roundmod,stsround);

			saldoTeoritis=saldoTeoritis-pokok;
			saldoTeoritis=rounding(saldoTeoritis,roundmod,stsround);
			angsur.put("angsuranPokok", BigDecimal.valueOf(pokok));
			angsur.put("angsuranBunga", BigDecimal.valueOf(bunga));
			angsur.put("totalAngsuran", BigDecimal.valueOf(totalAngsuran));
			angsur.put("saldoTeoritis", BigDecimal.valueOf(saldoTeoritis));
		}

		return listAngsuran;
	}

	public List angsuranBungaEfektif(List listAngsuran, BigDecimal maxCR, Integer roundmod, Integer stsround){
		double bakiDebet=maxCR.doubleValue();
		bakiDebet=rounding(bakiDebet,roundmod,stsround);
		for (int i = 1; i<listAngsuran.size(); i++) {
			DTOMap angsur=(DTOMap)listAngsuran.get(i);
			angsur.put("angsuranPokok", new BigDecimal(0));
			angsur.put("angsuranBunga", new BigDecimal(0));
			angsur.put("totalAngsuran", new BigDecimal(0));
			angsur.put("saldoTeoritis", BigDecimal.valueOf(bakiDebet));
		}

		return listAngsuran;
	}


	public double roundIrr(double irrPerPeriode, Integer periode){
		if(periode==0)
			periode=1;
		if((irrPerPeriode+"").equals("NaN"))
			throw new WrongValueException("Tidak dapat melakukan perhitungan.\nAngka melebihi batas maksimum.");
		irrPerPeriode=new BigDecimal(irrPerPeriode*(1200 / periode)).setScale(8, BigDecimal.ROUND_HALF_UP).doubleValue();
		return irrPerPeriode/(1200 / periode);
	}

	public List createArusKas(Integer jangkaWaktu, List listAngsuran, List listDiscount,
			BigDecimal maxCr, BigDecimal intRate, BigDecimal provFee, BigDecimal getFee, 
			Integer roundmod, Integer stsround, char intType, Integer periode, Integer grpBunga){
		return hitungArusKas(jangkaWaktu, listAngsuran, listDiscount,
				maxCr, intRate, provFee, getFee, 
				roundmod, stsround, intType, periode, grpBunga);
	}

	//if(intType=='A' //|| intType=='F' || intType=='L'
	//		|| intType=='G' || intType=='H'){
	//if(intType=='F' || intType=='L'){
	//	irr=intRate.doubleValue()/(jumlahHariTahunIrr/1)*maxHariBulan/100;
	//}else{
	//		irr=intRate.doubleValue()/(12/1)/100;
	//}
	//	irr=roundIrr(irr, 1);
	//} else {

	//}

	public List hitungArusKas(Integer jangkaWaktu, List listAngsuran, List listDiscount,
			BigDecimal maxCr, BigDecimal intRate, BigDecimal provFee, BigDecimal getFee, 
			Integer roundmod, Integer stsround, char intType, Integer periode, Integer grpBunga) {
		double atribusi=provFee.doubleValue()-getFee.doubleValue();
		//Menghitung Irr

		irr=0;
		irrAtribusi=0;
		irrDiskon=0;
		if(intType!='K'){
			if(listAngsuran.size()>0){
				if(intType=='A' || intType=='F'	|| intType=='G' || intType=='H' || intType=='L'){
					irr=intRate.doubleValue()/(12/1)/100;
				}else{
					double[] cashFlow=new double[listAngsuran.size()];
					cashFlow[0]=-maxCr.doubleValue();
					for (int i=1; i<listAngsuran.size(); i++) {
						DTOMap angsurIrr = (DTOMap) listAngsuran.get(i);
						cashFlow[i]=angsurIrr.getBigDecimal("totalAngsuran").doubleValue();
					}
					double getIrr = getIRR(cashFlow);
					if((getIrr+"").equals("NaN")){
						throw new WrongValueException("Tidak Bisa Ambil IRR. \n maximum number of iterations is exceeded.");
					}
					irr=getIrr/100;
					irr=roundIrr(irr, 1);
				}
				if(irr < 0){
					//throw new WrongValueException("nilai IRR negatif.");
					irr=0;
				}
				if(atribusi==0 && grpBunga==0){
					//bukan aruskas atribusi
				}else{
					if(provFee.doubleValue()+getFee.doubleValue() != 0){
						double[] cashFlowAtribusi=new double[listAngsuran.size()];
						cashFlowAtribusi[0]=(maxCr.doubleValue()-provFee.doubleValue()+getFee.doubleValue())*-1;
						for (int i=1; i<listAngsuran.size(); i++) {
							DTOMap angsurIrr = (DTOMap) listAngsuran.get(i);
							cashFlowAtribusi[i]=angsurIrr.getBigDecimal("totalAngsuran").doubleValue();
							System.out.println("=============angsurAN=========="+cashFlowAtribusi[i]);
						}
						irrAtribusi=getIRR(cashFlowAtribusi)/100;
						if((irrAtribusi+"").equals("NaN")){
							throw new WrongValueException("Tidak Bisa Ambil IRR Atribusi. \n maximum number of iterations is exceeded.");
						}
						irrAtribusi=roundIrr(irrAtribusi, 1);
						if(irrAtribusi < 0){
							throw new WrongValueException("nilai amortisasi lebih besar dari nilai bunga.");
						}
					}
				}
			}else{
				throw new WrongValueException("Jadwal Angsuran Tidak Terbentuk.");
			}
			if(listDiscount.size()>0){
				double[] cashFlow=new double[listDiscount.size()];
				cashFlow[0]=-maxCr.doubleValue();
				for (int i=1; i<listDiscount.size(); i++) {
					DTOMap angsurIrr = (DTOMap) listDiscount.get(i);
					cashFlow[i]=angsurIrr.getBigDecimal("totalAngsuran").doubleValue();
				}
				double getIrr = getIRR(cashFlow);
				if((getIrr+"").equals("NaN")){
					throw new WrongValueException("Tidak Bisa Ambil IRR Diskon. \n maximum number of iterations is exceeded.");
				}
				irrDiskon=getIrr/100;
				irrDiskon=roundIrr(irrDiskon, 1);
			}
		}
		//-------------------------------------------------------------------//

		double totalBunga=0;
		for (int i=1; i<listAngsuran.size(); i++) {
			DTOMap angsurIrr = (DTOMap) listAngsuran.get(i);
			totalBunga+=angsurIrr.getBigDecimal("angsuranBunga").doubleValue();
		}

		//Hitung Arus Kas
		int awalAruskas = 0;
		DTOMap angsur=(DTOMap)listAngsuran.get(0);
		if(caraCair == 1){
			SimpleDateFormat sdf = new SimpleDateFormat("MMyyyy");
			BigDecimal saldoTeoritis = new BigDecimal(0);
			for (int i = 0; i <=grpBunga; i++ ) {
				angsur = (DTOMap) listAngsuran.get(i);
				if(dataCair.get(sdf.format(angsur.get("tglJadwal")))!= null){
					BigDecimal saldoEst = (BigDecimal)dataCair.get(sdf.format(angsur.get("tglJadwal")));
					angsur.put("ESTIMASI_ARUS", saldoEst.negate());
					saldoTeoritis = saldoTeoritis.add(saldoEst);
				}else{
					angsur.put("ESTIMASI_ARUS", BigDecimal.ZERO);
				}
				angsur.put("SALDO_AWAL_KONVERSI", BigDecimal.ZERO);
				angsur.put("BUNGA_KONVERSI", BigDecimal.ZERO);
				angsur.put("POKOK_ARUS", BigDecimal.ZERO);
				angsur.put("BUNGA_ARUS", BigDecimal.ZERO);
				angsur.put("SELISIH_BUNGA_KONTRAKTUAL", BigDecimal.ZERO);
				angsur.put("SALDO_AKHIR_KONVERSI", saldoTeoritis);
			}
			awalAruskas = grpBunga+1;
		}else{
			angsur.put("ESTIMASI_ARUS", maxCr.negate());
			angsur.put("SALDO_AWAL_KONVERSI", BigDecimal.ZERO);
			angsur.put("BUNGA_KONVERSI", BigDecimal.ZERO);
			angsur.put("POKOK_ARUS", BigDecimal.ZERO);
			angsur.put("BUNGA_ARUS", BigDecimal.ZERO);
			angsur.put("SELISIH_BUNGA_KONTRAKTUAL", BigDecimal.ZERO);
			angsur.put("SALDO_AKHIR_KONVERSI", maxCr);
			awalAruskas = 1;
		}
		double totalBungaKonversi=0;
		double totalNilaiKiniArusKas=0;
		for (int i = awalAruskas; i <listAngsuran.size(); i++ ) {
			angsur = (DTOMap) listAngsuran.get(i);
			DTOMap angsurSebelum = (DTOMap) listAngsuran.get(i-1);

			BigDecimal pokok_arus=angsur.getBigDecimal("angsuranPokok").negate();
			BigDecimal bunga_arus=angsur.getBigDecimal("angsuranBunga").negate();
			BigDecimal estimasi_arus=rounding(pokok_arus.add(bunga_arus).abs(), roundmod, stsround);
			BigDecimal saldo_awal_konversi=angsurSebelum.getBigDecimal("SALDO_AKHIR_KONVERSI");

			BigDecimal bunga_konversi=BigDecimal.ZERO;
			if(intType!='K'){
				if(grpBunga==0 && (intType=='A' || intType=='D' || intType=='F' || intType=='G' || intType=='H' || intType=='L')){
					bunga_konversi=angsur.getBigDecimal("angsuranBunga");
				}else{
					bunga_konversi=rounding(new BigDecimal(saldo_awal_konversi.doubleValue()*irr), roundmod, stsround);
					if(i<listAngsuran.size()-1) totalBungaKonversi+=bunga_konversi.doubleValue();
					else bunga_konversi=new BigDecimal(totalBunga-totalBungaKonversi);
				}
			}

			BigDecimal selisih_bunga_kontraktual=rounding(bunga_konversi.add(bunga_arus), roundmod, stsround);
			BigDecimal saldo_akhir_konversi=rounding(saldo_awal_konversi.add(bunga_konversi).add(pokok_arus).add(bunga_arus), roundmod, stsround);

			angsur.put("ESTIMASI_ARUS", estimasi_arus);
			angsur.put("SALDO_AWAL_KONVERSI", saldo_awal_konversi);
			angsur.put("BUNGA_KONVERSI", bunga_konversi);
			angsur.put("POKOK_ARUS", pokok_arus);
			angsur.put("BUNGA_ARUS", bunga_arus);
			angsur.put("SELISIH_BUNGA_KONTRAKTUAL", selisih_bunga_kontraktual);
			angsur.put("SALDO_AKHIR_KONVERSI", saldo_akhir_konversi);

			if(listDiscount.size()>0){
				BigDecimal nilai_kini_arus = new BigDecimal(0);
				if(intType!='K'){
					nilai_kini_arus=rounding(new BigDecimal(estimasi_arus.doubleValue()/Math.pow((1+irrDiskon), i)), roundmod, stsround);
				}
				totalNilaiKiniArusKas+=nilai_kini_arus.doubleValue();
				angsur.put("NILAI_KINI_ARUS", nilai_kini_arus);
			}
		}

		//Hitung Arus Kas Attribusi
		if (atribusi!=0){
			int awalAruskasAtribusi = 0;
			double totalAmorAttr=0;
			if(caraCair == 1){
				SimpleDateFormat sdf = new SimpleDateFormat("MMyyyy");
				BigDecimal saldoTeoritis = new BigDecimal(0);
				for (int i = 0; i <=grpBunga; i++ ) {
					angsur = (DTOMap) listAngsuran.get(i);
					if(dataCair.get(sdf.format(angsur.get("tglJadwal")))!= null){
						BigDecimal saldoEst = (BigDecimal)dataCair.get(sdf.format(angsur.get("tglJadwal")));
						if(saldoTeoritis.doubleValue() == 0){
							angsur.put("ESTIMASI_ARUS_ATRIBUSI", saldoEst.subtract(provFee).add(getFee).negate());
							saldoTeoritis = saldoTeoritis.add(saldoEst.subtract(provFee).add(getFee));
						}else{
							angsur.put("ESTIMASI_ARUS_ATRIBUSI", saldoEst.negate());
							saldoTeoritis = saldoTeoritis.add(saldoEst);
						}

						angsur.put("SISA_ATRIBUSI", provFee.subtract(getFee));
					}else{
						angsur.put("ESTIMASI_ARUS_ATRIBUSI", BigDecimal.ZERO);
						angsur.put("SISA_ATRIBUSI", provFee.subtract(getFee));
					}
					angsur.put("SALDO_AKHIR_ARUS", saldoTeoritis);
					angsur.put("SALDO_AWAL_ARUS", BigDecimal.ZERO);
					angsur.put("BUNGA_ATRIBUSI", BigDecimal.ZERO);
					angsur.put("AMORTISASI_BIAYA_ATRIBUSI", BigDecimal.ZERO);
				}
				awalAruskasAtribusi = grpBunga+1;
			}else{
				angsur=(DTOMap)listAngsuran.get(0);
				angsur.put("ESTIMASI_ARUS_ATRIBUSI", maxCr.subtract(provFee).add(getFee).negate());
				angsur.put("SALDO_AWAL_ARUS", BigDecimal.ZERO);
				angsur.put("BUNGA_ATRIBUSI", BigDecimal.ZERO);
				angsur.put("AMORTISASI_BIAYA_ATRIBUSI", BigDecimal.ZERO);
				angsur.put("SALDO_AKHIR_ARUS", angsur.getBigDecimal("ESTIMASI_ARUS_ATRIBUSI").abs());
				angsur.put("SISA_ATRIBUSI", maxCr.subtract(angsur.getBigDecimal("SALDO_AKHIR_ARUS")));
				awalAruskasAtribusi = 1;
			}

			for (int i = awalAruskasAtribusi; i <listAngsuran.size(); i++ ) {
				angsur=(DTOMap)listAngsuran.get(i);
				DTOMap angsurSebelum=(DTOMap)listAngsuran.get(i-1);
				BigDecimal pokok_arus=angsur.getBigDecimal("angsuranPokok").negate();
				BigDecimal bunga_arus=angsur.getBigDecimal("angsuranBunga").negate();
				BigDecimal estimasi_arus_atribusi=angsur.getBigDecimal("ESTIMASI_ARUS");
				BigDecimal saldo_awal_arus=angsurSebelum.getBigDecimal("SALDO_AKHIR_ARUS");
				BigDecimal bunga_atribusi=rounding(new BigDecimal(saldo_awal_arus.doubleValue()*irrAtribusi), roundmod, stsround);

				double amor=bunga_atribusi.doubleValue()-angsur.getBigDecimal("BUNGA_KONVERSI").doubleValue();
				BigDecimal amortisasi_biaya_atribusi=rounding(new BigDecimal(amor), roundmod, stsround);
				if(i<listAngsuran.size()-1){
					totalAmorAttr+=rounding(amor, roundmod, stsround);
				} else {
					double selisih=(provFee.doubleValue()-getFee.doubleValue()-totalAmorAttr)-amortisasi_biaya_atribusi.doubleValue();
					amortisasi_biaya_atribusi=rounding(new BigDecimal(provFee.doubleValue()-getFee.doubleValue()-totalAmorAttr), roundmod, stsround);
					bunga_atribusi=bunga_atribusi.add(new BigDecimal(selisih));
				}
				BigDecimal saldo_akhir_arus=rounding(pokok_arus.add(bunga_arus).add(saldo_awal_arus).add(bunga_atribusi), roundmod, stsround);
				BigDecimal sisa_atribusi=rounding(angsurSebelum.getBigDecimal("SISA_ATRIBUSI").subtract(amortisasi_biaya_atribusi), roundmod, stsround);

				angsur.put("ESTIMASI_ARUS_ATRIBUSI", estimasi_arus_atribusi);
				angsur.put("SALDO_AWAL_ARUS", saldo_awal_arus);
				angsur.put("BUNGA_ATRIBUSI", bunga_atribusi);
				angsur.put("AMORTISASI_BIAYA_ATRIBUSI", amortisasi_biaya_atribusi);
				angsur.put("SALDO_AKHIR_ARUS", saldo_akhir_arus);
				angsur.put("SISA_ATRIBUSI", sisa_atribusi);
			}
		}

		//Hitung Arus Kas Discount
		if(listDiscount.size()>0){
			double totalAmorDisc=0;
			angsur=(DTOMap)listAngsuran.get(0);
			angsur.put("ETSIMASI_ARUS_DISCOUNT", maxCr.negate());
			angsur.put("SALDO_AWAL_DISCOUNT", BigDecimal.ZERO);
			angsur.put("BUNGA_DISCOUNT", BigDecimal.ZERO);
			angsur.put("AMORTISASI_DISCOUNT", BigDecimal.ZERO);
			angsur.put("SALDO_AKHIR_DISCOUNT", new BigDecimal(totalNilaiKiniArusKas));
			angsur.put("NILAI_KINI_ARUS", BigDecimal.ZERO);

			BigDecimal saldo_akhir_x;
			if(atribusi!=0)saldo_akhir_x=angsur.getBigDecimal("SALDO_AKHIR_ARUS");
			else saldo_akhir_x=angsur.getBigDecimal("SALDO_AKHIR_KONVERSI");
			angsur.put("SALDO_DISKON", rounding(saldo_akhir_x.subtract(new BigDecimal(totalNilaiKiniArusKas)), roundmod, stsround));

			for (int i = 1; i <listAngsuran.size(); i++ ) {
				angsur=(DTOMap)listAngsuran.get(i);
				DTOMap angsurSebelum=(DTOMap)listAngsuran.get(i-1);
				DTOMap disc=(DTOMap)listDiscount.get(i);

				BigDecimal bunga_x;
				if(atribusi!=0)bunga_x=angsur.getBigDecimal("BUNGA_ATRIBUSI");
				else bunga_x=angsur.getBigDecimal("BUNGA_KONVERSI");

				if(atribusi!=0)saldo_akhir_x=angsur.getBigDecimal("SALDO_AKHIR_ARUS");
				else saldo_akhir_x=angsur.getBigDecimal("SALDO_AKHIR_KONVERSI");

				BigDecimal pokok=angsur.getBigDecimal("POKOK_ARUS");
				BigDecimal bunga=angsur.getBigDecimal("BUNGA_ARUS");

				BigDecimal total_angsur_discount=disc.getBigDecimal("totalAngsuran");
				BigDecimal saldo_awal_discount=angsurSebelum.getBigDecimal("SALDO_AKHIR_DISCOUNT");
				BigDecimal bunga_discount=rounding(new BigDecimal(saldo_awal_discount.doubleValue()*irrDiskon), roundmod, stsround);
				BigDecimal saldo_akhir_discount=rounding(pokok.add(bunga).add(saldo_awal_discount).add(bunga_discount), roundmod, stsround);
				BigDecimal sisa_diskon=rounding(saldo_akhir_x.subtract(saldo_akhir_discount), roundmod, stsround);
				BigDecimal amortiasi_discount = new BigDecimal(0);
				if(i == listAngsuran.size()-1){
					amortiasi_discount = angsurSebelum.getBigDecimal("SALDO_DISKON");
					saldo_akhir_discount = new BigDecimal(0);
					sisa_diskon  = new BigDecimal(0);
				}else{
					amortiasi_discount=rounding(bunga_discount.subtract(bunga_x), roundmod, stsround);
				}

				//				totalAmorDisc+=amortiasi_discount.doubleValue();
				//				if(i==listAngsuran.size()-1){
				//					BigDecimal saldoDiskon=((DTOMap)listAngsuran.get(0)).getBigDecimal("SALDO_DISKON");
				//					double selisih=saldoDiskon.doubleValue()-Math.abs(totalAmorDisc);
				//					amortiasi_discount=amortiasi_discount.add(new BigDecimal(selisih));
				//					bunga_discount=bunga_discount.add(new BigDecimal(selisih));
				//					saldo_akhir_discount=rounding(pokok.add(bunga).add(saldo_awal_discount).add(bunga_discount), roundmod, stsround);
				//					sisa_diskon=rounding(saldo_akhir_x.subtract(saldo_akhir_discount).abs(), roundmod, stsround);
				//				}

				angsur.put("ETSIMASI_ARUS_DISCOUNT", total_angsur_discount);
				angsur.put("SALDO_AWAL_DISCOUNT", saldo_awal_discount);
				angsur.put("BUNGA_DISCOUNT", bunga_discount);
				angsur.put("AMORTISASI_DISCOUNT", amortiasi_discount);
				angsur.put("SALDO_AKHIR_DISCOUNT", saldo_akhir_discount);
				angsur.put("SALDO_DISKON", sisa_diskon);
			}
		}

		return listAngsuran;
	}

	public List hitungArusKasImpairment(Integer jangkaWaktu, List listAngsuran, List listDiscount,
			BigDecimal maxCr, BigDecimal intRate, BigDecimal provFee, BigDecimal getFee, 
			Integer roundmod, Integer stsround, char intType, Integer periode, Integer grpBunga,BigDecimal irr2) {
		double atribusi=provFee.doubleValue()-getFee.doubleValue();
		//Menghitung Irr

		irr=0;
		irrAtribusi=0;
		irrDiskon=0;
		if(listAngsuran.size()>0){
			if(intType=='A' || intType=='F'	|| intType=='G' || intType=='H' || intType=='L'){
				irr=intRate.doubleValue()/(12/1)/100;
			}else{
				double[] cashFlow=new double[listAngsuran.size()];
				cashFlow[0]=-maxCr.doubleValue();
				for (int i=1; i<listAngsuran.size(); i++) {
					DTOMap angsur = (DTOMap) listAngsuran.get(i);
					cashFlow[i]=angsur.getBigDecimal("totalAngsuran").doubleValue();
				}
				double getIrr = getIRR(cashFlow);
				if((getIrr+"").equals("NaN")){
					throw new WrongValueException("Tidak Bisa Ambil IRR. \n maximum number of iterations is exceeded.");
				}
				irr=getIrr/100;
				irr=roundIrr(irr, 1);
			}
			if(irr < 0){
				throw new WrongValueException("nilai IRR negatif.");
			}
			if(atribusi==0 && grpBunga==0){
				//buka arus kas atribusi
			}else{
				if(provFee.doubleValue()+getFee.doubleValue() != 0){
					double[] cashFlowAtribusi=new double[listAngsuran.size()];
					cashFlowAtribusi[0]=(maxCr.doubleValue()-provFee.doubleValue()+getFee.doubleValue())*-1;
					for (int i=1; i<listAngsuran.size(); i++) {
						DTOMap angsur = (DTOMap) listAngsuran.get(i);
						cashFlowAtribusi[i]=angsur.getBigDecimal("totalAngsuran").doubleValue();
					}
					irrAtribusi=getIRR(cashFlowAtribusi)/100;
					if((irrAtribusi+"").equals("NaN")){
						throw new WrongValueException("Tidak Bisa Ambil IRR Atribusi. \n maximum number of iterations is exceeded.");
					}
					irrAtribusi=roundIrr(irrAtribusi, 1);
					if(irrAtribusi < 0){
						throw new WrongValueException("nilai amortisasi lebih besar dari nilai bunga.");
					}
				}
			}
		}else{
			throw new WrongValueException("Jadwal Angsuran Tidak Terbentuk.");
		}
		if(listDiscount.size()>0){
			double[] cashFlow=new double[listDiscount.size()];
			cashFlow[0]=-maxCr.doubleValue();
			for (int i=1; i<listDiscount.size(); i++) {
				DTOMap angsur = (DTOMap) listDiscount.get(i);
				cashFlow[i]=angsur.getBigDecimal("totalAngsuran").doubleValue();
			}
			double getIrr = getIRR(cashFlow);
			if((getIrr+"").equals("NaN")){
				throw new WrongValueException("Tidak Bisa Ambil IRR Diskon. \n maximum number of iterations is exceeded.");
			}
			irrDiskon=getIrr/100;
			irrDiskon=roundIrr(irrDiskon, 1);
		}
		//-------------------------------------------------------------------//

		double totalBunga=0;
		for (int i=1; i<listAngsuran.size(); i++) {
			DTOMap angsur = (DTOMap) listAngsuran.get(i);
			totalBunga+=angsur.getBigDecimal("angsuranBunga").doubleValue();
		}

		//Hitung Arus Kas
		DTOMap angsur=(DTOMap)listAngsuran.get(0);
		angsur.put("ESTIMASI_ARUS", maxCr.negate());
		angsur.put("SALDO_AWAL_KONVERSI", BigDecimal.ZERO);
		angsur.put("BUNGA_KONVERSI", BigDecimal.ZERO);
		angsur.put("POKOK_ARUS", BigDecimal.ZERO);
		angsur.put("BUNGA_ARUS", BigDecimal.ZERO);
		angsur.put("SELISIH_BUNGA_KONTRAKTUAL", BigDecimal.ZERO);
		angsur.put("SALDO_AKHIR_KONVERSI", maxCr);
		angsur.put("NILAI_KINI_ARUS", BigDecimal.ZERO);

		//untuk ambil nilai kini
		double totalNilaiKiniArusKas=0;
		for (int i = 1; i <listAngsuran.size(); i++ ) {
			angsur = (DTOMap) listAngsuran.get(i);
			DTOMap angsurSebelum = (DTOMap) listAngsuran.get(i-1);

			BigDecimal pokok_arus=angsur.getBigDecimal("angsuranPokok").negate();
			BigDecimal bunga_arus=angsur.getBigDecimal("angsuranBunga").negate();
			BigDecimal estimasi_arus=pokok_arus.add(bunga_arus).abs();
			BigDecimal nilai_kini_arus=rounding(new BigDecimal(estimasi_arus.doubleValue()/Math.pow((1+irr2.doubleValue()/100), i)), 2, stsround);
			if (i==listAngsuran.size()-1) {
				nilai_kini_arus = maxCr.subtract(provFee).add(getFee).subtract(new BigDecimal(totalNilaiKiniArusKas));
				System.out.println("nilai_kini_arus = "+totalNilaiKiniArusKas+ " maxCr "+ maxCr);
			}
			totalNilaiKiniArusKas+=nilai_kini_arus.doubleValue();
			angsur.put("NILAI_KINI_ARUS", nilai_kini_arus);
		}
		//untuk ambil nilai kini

		DTOMap angsurAwal=(DTOMap)listAngsuran.get(0);
		angsurAwal.put("ESTIMASI_ARUS", new BigDecimal(totalNilaiKiniArusKas*-1));
		angsurAwal.put("SALDO_AWAL_KONVERSI", BigDecimal.ZERO);
		angsurAwal.put("BUNGA_KONVERSI", BigDecimal.ZERO);
		angsurAwal.put("POKOK_ARUS", BigDecimal.ZERO);
		angsurAwal.put("BUNGA_ARUS", BigDecimal.ZERO);
		angsurAwal.put("SELISIH_BUNGA_KONTRAKTUAL", BigDecimal.ZERO);
		angsurAwal.put("SALDO_AKHIR_KONVERSI", new BigDecimal(totalNilaiKiniArusKas));
		double totalBungaKonversi=0;
		double totalEstimasi = angsurAwal.getBigDecimal("ESTIMASI_ARUS").doubleValue();
		for (int i = 1; i <listAngsuran.size(); i++ ) {
			angsur = (DTOMap) listAngsuran.get(i);
			DTOMap angsurSebelum = (DTOMap) listAngsuran.get(i-1);

			BigDecimal pokok_arus=angsur.getBigDecimal("angsuranPokok").negate();
			BigDecimal bunga_arus=angsur.getBigDecimal("angsuranBunga").negate();
			BigDecimal estimasi_arus=rounding(pokok_arus.add(bunga_arus).abs(), roundmod, stsround);
			totalEstimasi += estimasi_arus.doubleValue();
			BigDecimal saldo_awal_konversi=angsurSebelum.getBigDecimal("SALDO_AKHIR_KONVERSI");
			BigDecimal bunga_konversi=rounding(new BigDecimal(saldo_awal_konversi.doubleValue()*(irr2.doubleValue()/100)), roundmod, stsround);
			totalBungaKonversi+=bunga_konversi.doubleValue();

			if(i == listAngsuran.size()-1){
				double selisih = totalEstimasi - totalBungaKonversi;
				System.out.println("BUNGA_KONVERSI"+bunga_konversi+" SELISIH "+selisih+" TOTAL ESTIMASI "+totalEstimasi+" TOTAL BUNGA KONVERSI "+totalBungaKonversi);
				bunga_konversi = bunga_konversi.add(new BigDecimal(selisih));
			}

			BigDecimal selisih_bunga_kontraktual=rounding(bunga_konversi.add(bunga_arus), roundmod, stsround);
			BigDecimal saldo_akhir_konversi=rounding(saldo_awal_konversi.add(bunga_konversi).add(pokok_arus).add(bunga_arus), roundmod, stsround);

			angsur.put("ESTIMASI_ARUS", estimasi_arus);
			angsur.put("SALDO_AWAL_KONVERSI", saldo_awal_konversi);
			angsur.put("BUNGA_KONVERSI", bunga_konversi);
			angsur.put("POKOK_ARUS", pokok_arus);
			angsur.put("BUNGA_ARUS", bunga_arus);
			angsur.put("SELISIH_BUNGA_KONTRAKTUAL", selisih_bunga_kontraktual);
			angsur.put("SALDO_AKHIR_KONVERSI", saldo_akhir_konversi);

		}
		return listAngsuran;
	}

	public static double roundingUp(double angkaD, int scale){
		BigDecimal angka=BigDecimal.valueOf(angkaD);
		return angka.setScale(scale, RoundingMode.HALF_UP).doubleValue();
	}

	public static BigDecimal rounding(BigDecimal angkaD, Integer model, Integer status) {
		return new BigDecimal(rounding(angkaD.doubleValue(),model,status));
	}

	public static double rounding(double angkaD, Integer model, Integer status) {
		BigDecimal angka=BigDecimal.valueOf(angkaD);
		switch (model) {
		case 0:
			if (status == 0) {
				return angka.setScale(0, RoundingMode.DOWN).doubleValue();
			} else if(status == 1){
				return angka.setScale(0, RoundingMode.UP).doubleValue();
			} else if (status == 2){
				return angka.setScale(0, RoundingMode.HALF_UP).doubleValue();
			} else if (status == 3){
				return angka.setScale(0, RoundingMode.HALF_EVEN).doubleValue();
			}
		case 1:
			if (status == 0) {
				return angka.setScale(1, RoundingMode.DOWN).doubleValue();
			} else if (status == 1){
				return angka.setScale(1, RoundingMode.UP).doubleValue();
			} else if (status == 2){
				return angka.setScale(1, RoundingMode.HALF_UP).doubleValue();
			} else if (status == 3){
				return angka.setScale(1, RoundingMode.HALF_EVEN).doubleValue();
			}
		case 2:
			if (status == 0) {
				return angka.setScale(2, RoundingMode.DOWN).doubleValue();
			} else if (status == 1){
				return angka.setScale(2, RoundingMode.UP).doubleValue();
			} else if (status == 2){
				return angka.setScale(2, RoundingMode.HALF_UP).doubleValue();
			} else if (status == 3){
				return angka.setScale(2, RoundingMode.HALF_EVEN).doubleValue();
			}
		case 3:
			if (status == 0) {
				return angka.setScale(3, RoundingMode.DOWN).doubleValue();
			} else if (status == 1){
				return angka.setScale(3, RoundingMode.UP).doubleValue();
			} else if (status == 2){
				return angka.setScale(3, RoundingMode.HALF_UP).doubleValue();
			} else if (status == 3){
				return angka.setScale(3, RoundingMode.HALF_EVEN).doubleValue();
			}
		case 4:
			if (status == 0) {
				return angka.setScale(-1, RoundingMode.DOWN).doubleValue();
			} else if (status == 1){
				return angka.setScale(-1, RoundingMode.UP).doubleValue();
			} else if (status == 2){
				return angka.setScale(-1, RoundingMode.HALF_UP).doubleValue();
			} else if (status == 3){
				return angka.setScale(-1, RoundingMode.HALF_EVEN).doubleValue();
			}
		case 5:
			if (status == 0) {
				return angka.setScale(-2, RoundingMode.DOWN).doubleValue();
			} else if (status == 1){
				return angka.setScale(-2, RoundingMode.UP).doubleValue();
			} else if (status == 2){
				return angka.setScale(-2, RoundingMode.HALF_UP).doubleValue();
			} else if (status == 3){
				return angka.setScale(-2, RoundingMode.HALF_EVEN).doubleValue();
			}
		case 6:
			if (status == 0) {
				return angka.setScale(-3, RoundingMode.DOWN).doubleValue();
			} else if (status == 1){
				return angka.setScale(-3, RoundingMode.UP).doubleValue();
			} else if (status == 2){
				return angka.setScale(-3, RoundingMode.HALF_UP).doubleValue();
			} else if (status == 3){
				return angka.setScale(-3, RoundingMode.HALF_EVEN).doubleValue();
			}
		case 7:
			if (status == 0) {
				return angka.setScale(0, RoundingMode.DOWN).doubleValue();
			} else if (status == 1){
				return angka.setScale(0, RoundingMode.UP).doubleValue();
			} else if (status == 2){
				return angka.setScale(0, RoundingMode.HALF_UP).doubleValue();
			} else if (status == 3){
				return angka.setScale(0, RoundingMode.HALF_EVEN).doubleValue();
			}

		default:
			break;
		}
		return angka.doubleValue();
	}

	public double getIrr() {
		return irr;
	}

	public double getIrrAtribusi() {
		return irrAtribusi;
	}

	public double getIrrDiskon() {
		return irrDiskon;
	}

	public void setIrr(double irr) {
		this.irr = irr;
	}
}