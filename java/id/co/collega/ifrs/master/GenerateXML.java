package id.co.collega.ifrs.master;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.master.service.MasterServices;
import id.co.collega.ifrs.util.MessageBox;

public class GenerateXML implements Runnable {

	private static final Logger log=LoggerFactory.getLogger(GenerateXML.class);
	
		
	@Autowired
	MasterServices masterServices;

	private List<DTOMap> ls1;

	@Autowired
	Environment env;

	private String filePendukung;
	private String pathFile;

	private String namaFileXML;

	private Document doc;

	private DocumentBuilderFactory docFactory;

	private Object docBuilder;

	private String dbKr;

	private Element elementLtkt;

	// private DTOMap dtoDataLTKT;

	private String idtrxProvinsi;

	private String idtrxKab;

	private SimpleDateFormat sdf;

	GenerateXML(MasterServices masterService, Environment env) {
		this.masterServices = masterService;
		this.env = env;
	}

	GenerateXML() {

	}

	private void generate() {
		try {
			String sql;
			// sql = " SELECT * FROM PPATK_EXCEL_RINCI ";
			// sql="SELECT * FROM PPATK_EXCEL_RINCI WHERE CIFID='0000049278' ";
			// 0000002343
			// sql="SELECT * FROM PPATK_EXCEL_RINCI WHERE CIFID='0000002343' ";
			// sql="SELECT * FROM PPATK_EXCEL_RINCI FETCH first 10 ROWS only";
			// System.out.println("test run sudah jalan");
			/*
			 * sql=
			 * " SELECT DISTINCT( a.CIFID), a.NAMA_LENGKAP,a.TANGGAL_TX as TANGGAL_TRANSAKSI, a.COUNT, a.NILAI, b.KD_JENIS_NASABAH,"
			 * + "a.DBKR,  b.NAMA_CABANG_TEMPAT_TERJADINYA_TRANSAKSI, b.NOMOR_REKENING " +
			 * " FROM PPATK_EXCEL a  " +
			 * "INNER JOIN PPATK_EXCEL_RINCI b ON a.cifid=b.cifid AND a.TANGGAL_TX=b.TANGGAL_TRANSAKSI AND a.DBKR=b.DBKR "
			 * ;
			 */
			// + "where a.cifid= '0000049278' ";
			String envCatalina = env.getProperty("PATH_ZIP_APUPPT").toString() + "\\bin\\";
			log.info("Catalina Home : {} ", envCatalina);
			
			String envCatalina2 = System.getenv("CATALINA_HOME") + "\\bin\\";
			log.info("Catalina Home : {} ", envCatalina2);
			
			String envCatalina3 = System.getenv("JAVA_HOME") + "\\bin\\";
			log.info("Java Home : {} ", envCatalina3);
			
			
			
			sql = "select * from PPATK_EXCEL ";

			ls1 = masterServices.getDataMaster(sql, null);

			if (ls1.size() > 0 || !ls1.isEmpty()) {

				for (DTOMap map : ls1) {
					try {
						sdf = new SimpleDateFormat("yyyy-MM-dd");
						SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
						
						log.info("\n\t------------------------\n\t"+
								"CIFID awal  : {} \n\t"+
								"TANGGAL_TX  : {} \n\t"+
								"DBKR        : {} \n\t"+
								"------------------------\n\t",
								map.getString("CIFID"),
								sdf.format(map.getDate("TANGGAL_TX")),
								map.getString("DBKR")
								);
						
												
						if (map.getString("DBKR").equals("D")) {
							dbKr = "0";
						} else {
							dbKr = "1";
						}
						sql = "SELECT DISTINCT (KD_JENIS_NASABAH)  FROM PPATK_EXCEL_RINCI WHERE CIFID=? AND TANGGAL_TRANSAKSI =? AND DBKR=?";
						DTOMap dMapKode = masterServices.getMapMaster(sql, new Object[] { map.getString("CIFID"),
								sdf.format(map.getDate("TANGGAL_TX")), map.getString("DBKR") });
						log.info("KD_jenis Nasabah : {} \n\t",dMapKode.getString("KD_JENIS_NASABAH"));

						namaFileXML = sdf1.format(map.getDate("TANGGAL_TX")) + "_"
								+ dMapKode.getString("KD_JENIS_NASABAH") + "_" + dbKr + "_" + map.getString("CIFID");
						docFactory = DocumentBuilderFactory.newInstance();
						docBuilder = null;
						try {
							docBuilder = docFactory.newDocumentBuilder();
						} catch (ParserConfigurationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						doc = ((DocumentBuilder) docBuilder).newDocument();

						
						String localId = sdf1.format(map.getDate("TANGGAL_TX")) + map.getString("CIFID") + dbKr
								+ dMapKode.getString("KD_JENIS_NASABAH");
						/*
						 * ELEMENT LTKT
						 */
						elementLtkt = doc.createElement("ltkt");
						elementLtkt.appendChild(setElement("localId", setDataXML(localId), doc));
						doc.appendChild(elementLtkt);
						
						/*
						 * ELEMENT UMUM
						 */
						Element elementUmum = doc.createElement("umum");
						elementUmum.appendChild(setElement("tglLaporan", setDataXML(sdf.format(map.getDate("TANGGAL_TX"))), doc));
						elementUmum.appendChild(setElement("namaPejabat", setDataXML("Alfriets Kawengian"), doc));
						elementUmum.appendChild(setElement("Laporan", setDataXML("1"), doc));
						elementUmum.appendChild(setElement("noLtktKoreksi", setDataXML(""), doc));
						String str = "Tindaklanjut audit Khusus PPATK tahun "
								+ new SimpleDateFormat("yyyy").format(map.getDate("TANGGAL_TX"));
						elementUmum.appendChild(setElement("informasiLain", setDataXML(str), doc));
						elementLtkt.appendChild(elementUmum);
						
						
						setXMLIdentitasTerlapor(doc, elementLtkt, map, dMapKode);
						
						
						sql = "SELECT * FROM PPATK_EXCEL_RINCI WHERE CIFID=? AND TANGGAL_TRANSAKSI =? AND DBKR=? ";
						List<DTOMap> lsDtoMap = masterServices.getDataMaster(sql, new Object[] { map.getString("CIFID"),
								sdf.format(map.getDate("TANGGAL_TX")), map.getString("DBKR") });
						if (lsDtoMap.size() > 0 || !lsDtoMap.isEmpty()) {
							for (DTOMap mapData : lsDtoMap) {
								Element elementTransaksi = doc.createElement("transaksi");

								elementTransaksi.appendChild(setElement("tglTransaksi",
										setDataXML(sdf.format(map.getDate("TANGGAL_TX"))), doc));
								elementTransaksi.appendChild(setElement("pjkTempatKejadian",
										setDataXML(mapData.getString("NAMA_CABANG_TEMPAT_TERJADINYA_TRANSAKSI")), doc));
								// dtoDataLTKT
								elementTransaksi.appendChild(setElement("idPropinsi", setDataXML(idtrxProvinsi), doc));
								// idtrxProvinsi=dtoDataLTKT.getString("IDPROPINSI");
								// idtrxKab=dtoDataLTKT.getString("IDKOTAKAB");
								elementTransaksi.appendChild(setElement("idKotaKab", setDataXML(idtrxKab), doc));
								elementTransaksi.appendChild(setElement("nilaiTransaksi",
										setDataXML(String.valueOf(map.getBigDecimal("NILAI").setScale(0))), doc));
								elementTransaksi
										.appendChild(setElement("stat", setDataXML(String.valueOf(map.getInt("COUNT"))), doc));

								Element elementDetailTrx = doc.createElement("detailTransaksi");
								elementDetailTrx.appendChild(setElement("nilaiTransaksi",
										setDataXML(String.valueOf(mapData.getBigDecimal("NILAI_TRANSAKSI_RP").setScale(0))), doc));
								elementTransaksi.appendChild(elementDetailTrx);

								if (mapData.getString("KD_JENIS_NASABAH").equals("1")) {
									detailTransaksiNonPerorangan(map, elementTransaksi);
								} else {
									detailTransaksiPerorangan(map, elementTransaksi);
								}

								elementLtkt.appendChild(elementTransaksi);
							}
						}
						
						
						TransformerFactory transformerFactory = TransformerFactory.newInstance();
						Transformer transformer = null;
						try {
							transformer = transformerFactory.newTransformer();
						} catch (TransformerConfigurationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						DOMSource source = new DOMSource(doc);

						StringWriter writer = new StringWriter();
						StreamResult result = new StreamResult(writer);
						try {
							transformer.transform(source, result);
						} catch (TransformerException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// writer.flush();

						/*
						 * DOWNLOAD FILE XML
						 */
						// Filedownload.save(writer.toString().getBytes(), "xml", namaFileXML + ".xml");

						String path = env.getProperty("PATH_XML_APUPPT").toString() + File.separatorChar;
						String pathZip = env.getProperty("PATH_ZIP_APUPPT").toString() + File.separatorChar;
						String pathFileXml = path + namaFileXML + ".xml";
						String pathFileZip = pathZip + namaFileXML + ".zip";
						FileWriter fw = new FileWriter(pathFileXml);
						/*
						 * StringWriter sw = new StringWriter(); sw.write("some content...");
						 */
						fw.write(writer.toString());
						fw.close();

						// File f=new File(pathFileXml);
						FileOutputStream fos = new FileOutputStream(pathFileZip);
						ZipOutputStream zos = new ZipOutputStream(fos);
						ZipEntry ze = new ZipEntry(namaFileXML + ".xml");
						zos.putNextEntry(ze);

						byte[] buffer = new byte[1024];

						FileInputStream in = new FileInputStream(pathFileXml);

						int len;
						while ((len = in.read(buffer)) > 0) {
							zos.write(buffer, 0, len);
						}

						in.close();
						zos.closeEntry();

						// remember close it
						zos.close();
						readFileName();
						
						
						
						
						

					} catch (Exception e) {
						e.printStackTrace();
					}/*catch(NullPointerException e) {
						log.error(e.printStackTrace());
					}*/

					
				}

			} else {
				MessageBox.showInformation("Data Kosong ...!");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	

	private void detailTransaksiPerorangan(DTOMap mapData, Element elementTransaksi) {
		Element elementDetailTrxPerorangan = doc.createElement("terkaitPerorangan");
		String sql = "SELECT * FROM DBO.PPATK_CIFPERSNL WHERE CIFID=? ";
		//System.out.println("detailTransaksiPerorangan CIFID :" + mapData.getString("CIFID"));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		DTOMap dtPero = masterServices.getMapMaster(sql, new Object[] { mapData.getString("CIFID") });

		elementDetailTrxPerorangan.appendChild(setElement("gelar", setDataXML(""), doc));
		elementDetailTrxPerorangan
				.appendChild(setElement("namaLengkap", setDataXML(dtPero.getString("NAMALENGKAP")), doc));
		elementDetailTrxPerorangan
				.appendChild(setElement("tempatLahir", setDataXML(dtPero.getString("TEMPATLAHIR")), doc));
		elementDetailTrxPerorangan
				.appendChild(setElement("tglLahir", setDataXML(sdf.format(dtPero.getDate("TGLLAHIR"))), doc));
		elementDetailTrxPerorangan
				.appendChild(setElement("wargaNegara", setDataXML(dtPero.getString("WARGANEGARA")), doc));
		elementDetailTrxPerorangan
				.appendChild(setElement("idWargaAsal", setDataXML(dtPero.getString("IDNEGARAASAL")), doc));
		elementDetailTrxPerorangan.appendChild(setElement("namaJalan", setDataXML(dtPero.getString("NAMAJALAN")), doc));
		elementDetailTrxPerorangan.appendChild(setElement("rt", setDataXML(dtPero.getString("RT")), doc));
		elementDetailTrxPerorangan.appendChild(setElement("rw", setDataXML(dtPero.getString("RW")), doc));
		elementDetailTrxPerorangan
				.appendChild(setElement("idKelurahan", setDataXML(dtPero.getString("IDKELURAHAN")), doc));
		elementDetailTrxPerorangan
				.appendChild(setElement("idKecamatan", setDataXML(dtPero.getString("IDKECAMATAN")), doc));
		elementDetailTrxPerorangan.appendChild(setElement("kodePos", setDataXML(dtPero.getString("KODEPOS")), doc));
		elementDetailTrxPerorangan.appendChild(setElement("idKotaKab", setDataXML(dtPero.getString("IDKOTAKAB")), doc));
		elementDetailTrxPerorangan
				.appendChild(setElement("idPropinsi", setDataXML(dtPero.getString("IDPROPINSI")), doc));
		elementDetailTrxPerorangan
				.appendChild(setElement("idNegara", setDataXML(dtPero.getString("IDNEGARAASAL")), doc));
		elementDetailTrxPerorangan
				.appendChild(setElement("namaJalanIdentitas", setDataXML(dtPero.getString("NAMAJALAN")), doc));
		elementDetailTrxPerorangan.appendChild(setElement("rtIdentitas", setDataXML(dtPero.getString("RT")), doc));
		elementDetailTrxPerorangan.appendChild(setElement("rwIdentitas", setDataXML(dtPero.getString("RW")), doc));
		elementDetailTrxPerorangan
				.appendChild(setElement("idKelurahanIdentitas", setDataXML(dtPero.getString("IDKELURAHAN")), doc));
		elementDetailTrxPerorangan
				.appendChild(setElement("idKecamatanIdentitas", setDataXML(dtPero.getString("IDKECAMATAN")), doc));
		elementDetailTrxPerorangan
				.appendChild(setElement("kodePosIdentitas", setDataXML(dtPero.getString("KODEPOS")), doc));
		elementDetailTrxPerorangan
				.appendChild(setElement("idKotaKabIdentitas", setDataXML(dtPero.getString("IDKOTAKAB")), doc));
		elementDetailTrxPerorangan
				.appendChild(setElement("idPropinsiIdentitas", setDataXML(dtPero.getString("IDPROPINSI")), doc));
		elementDetailTrxPerorangan
				.appendChild(setElement("idNegaraIdentitas", setDataXML(dtPero.getString("IDNEGARAASAL")), doc));
		elementDetailTrxPerorangan.appendChild(setElement("namaJalanNegaraAsal", setDataXML(""), doc));
		elementDetailTrxPerorangan.appendChild(setElement("kodePosNegaraAsal", setDataXML(""), doc));
		elementDetailTrxPerorangan.appendChild(setElement("kotaNegaraAsal", setDataXML(""), doc));
		// 91=> papua
		// 92=>paupa barat
		// 93=> lainnya
		String provinsiNeagaraAsal;
		if (dtPero.getString("IDPROPINSI").equals("91")) {
			provinsiNeagaraAsal = "Papua";
		}
		if (dtPero.getString("IDPROPINSI").equals("92")) {
			provinsiNeagaraAsal = "Papua Barat";
		} else {
			provinsiNeagaraAsal = "lainnya";
		}

		elementDetailTrxPerorangan.appendChild(setElement("propinsiNegaraAsal", setDataXML(provinsiNeagaraAsal), doc));
		elementDetailTrxPerorangan.appendChild(setElement("negaraAsal", setDataXML(""), doc));
		elementDetailTrxPerorangan.appendChild(setElement("ktp", setDataXML(dtPero.getString("KTP")), doc));
		elementDetailTrxPerorangan.appendChild(setElement("sim", setDataXML(dtPero.getString("SIM")), doc));
		elementDetailTrxPerorangan.appendChild(setElement("passport", setDataXML(""), doc));
		elementDetailTrxPerorangan.appendChild(setElement("kimsKitasKitap", setDataXML(""), doc));
		elementDetailTrxPerorangan.appendChild(setElement("buktiLain", setDataXML(dtPero.getString("BUKTILAIN")), doc));
		elementDetailTrxPerorangan.appendChild(setElement("noBuktiLain", setDataXML(""), doc));
		elementDetailTrxPerorangan.appendChild(setElement("npwp", setDataXML(""), doc));
		elementDetailTrxPerorangan.appendChild(setElement("pekerjaanUtama", setDataXML(""), doc));
		elementDetailTrxPerorangan.appendChild(setElement("jabatan", setDataXML(""), doc));
		elementDetailTrxPerorangan.appendChild(setElement("penghasilan", setDataXML(""), doc));
		elementDetailTrxPerorangan.appendChild(setElement("tempatKerja", setDataXML(""), doc));
		elementDetailTrxPerorangan.appendChild(setElement("tujuanTransaksi", setDataXML(""), doc));
		elementDetailTrxPerorangan.appendChild(setElement("sumberDana", setDataXML(""), doc));
		elementDetailTrxPerorangan.appendChild(setElement("namaBankLain", setDataXML(""), doc));
		elementDetailTrxPerorangan.appendChild(setElement("noRekeningTujuan", setDataXML(""), doc));
		elementDetailTrxPerorangan.appendChild(setElement("noRekeningLain", setDataXML(""), doc));

		elementTransaksi.appendChild(elementDetailTrxPerorangan);

	}

	private void detailTransaksiNonPerorangan(DTOMap mapData, Element elementTransaksi) {
		Element elementDetailTrxKoporasi = doc.createElement("terkaitKorporasi");
		String sql = " SELECT * FROM PPATK_CIFCOM WHERE CIFID= ?";
		// System.out.println(" detailTransaksiNonPerorangan CIFID :
		// "+mapData.get("CIFID"));
		DTOMap dtKorpor = masterServices.getMapMaster(sql, new Object[] { mapData.get("CIFID") });

		elementDetailTrxKoporasi
				.appendChild(setElement("namaKorporasi", setDataXML(dtKorpor.getString("NAMAKORPORASI")), doc));
		elementDetailTrxKoporasi
				.appendChild(setElement("bentukBadan", setDataXML(dtKorpor.getString("BENTUKBADAN")), doc));
		// elementDetailTrxKoporasi.appendChild(setElement("bidangUsaha",
		// setDataXML(dtKorpor.getString("BIDANGUSAHA")), doc));
		elementDetailTrxKoporasi.appendChild(setElement("bidangUsaha", setDataXML("22"), doc));
		elementDetailTrxKoporasi
				.appendChild(setElement("bidangUsahaLain", setDataXML(dtKorpor.getString("BIDANGUSAHALAIN")), doc));
		elementDetailTrxKoporasi.appendChild(setElement("rt", setDataXML(dtKorpor.getString("RT")), doc));
		elementDetailTrxKoporasi.appendChild(setElement("rw", setDataXML(dtKorpor.getString("RW")), doc));
		elementDetailTrxKoporasi
				.appendChild(setElement("idKelurahan", setDataXML(dtKorpor.getString("IDKELURAHAN")), doc));
		elementDetailTrxKoporasi
				.appendChild(setElement("idKecamatan", setDataXML(dtKorpor.getString("IDKECAMATAN")), doc));
		elementDetailTrxKoporasi.appendChild(setElement("kodePos", setDataXML(dtKorpor.getString("KODEPOS")), doc));
		elementDetailTrxKoporasi.appendChild(setElement("idKotaKab", setDataXML(dtKorpor.getString("IDKOTAKAB")), doc));
		elementDetailTrxKoporasi
				.appendChild(setElement("idPropinsi", setDataXML(dtKorpor.getString("IDPROPINSI")), doc));
		elementDetailTrxKoporasi.appendChild(setElement("idNegara", setDataXML(dtKorpor.getString("IDNEGARA")), doc));
		elementDetailTrxKoporasi.appendChild(setElement("npwp", setDataXML(dtKorpor.getString("NPWP")), doc));
		elementDetailTrxKoporasi.appendChild(setElement("tujuanTransaksi", setDataXML(""), doc));
		elementDetailTrxKoporasi.appendChild(setElement("sumberDana", setDataXML(""), doc));
		elementDetailTrxKoporasi.appendChild(setElement("namaBankLain", setDataXML(""), doc));
		elementDetailTrxKoporasi.appendChild(setElement("noRekeningTujuan", setDataXML(""), doc));

		elementTransaksi.appendChild(elementDetailTrxKoporasi);

	}

	private Element setElement(String name, String value, Document doc) {
		Element element = doc.createElement(name);
		element.appendChild(doc.createTextNode(value));
		return element;
	}

	private String setDataXML(Date val) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if (val != null) {
				return sdf.format(val);
			}

			return "1900-01-01";
		} catch (Exception e) {
			e.printStackTrace();
			return "1900-01-01";
		}
	}

	private String setDataXML(String val) {
		if (val != null && !val.trim().equals("")) {
			return val;
		}

		return "";
	}

	private void setXMLIdentitasTerlapor(Document doc, Element elementLtkt, DTOMap dtoMap, DTOMap dtKode) {
		try {
			Element elementTerlapor;
			if (dtKode.getString("KD_JENIS_NASABAH").equals("0")) {
				elementTerlapor = doc.createElement("perorangan");
				setXMLPerorangan(elementTerlapor, doc, dtoMap);
			} else {
				elementTerlapor = doc.createElement("korporasi");
				setXMLKorporasi(elementTerlapor, doc, dtoMap);
			}

			// setXMLIdentitas(elementTerlapor, doc, dtoMap);
			elementLtkt.appendChild(elementTerlapor);
			// doc.appendChild(arg0)
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setXMLPerorangan(Element elementTerlapor, Document doc, DTOMap dtoMap) {
		try {
			String sql = "SELECT * FROM PPATK_CIFPERSNL WHERE cifid= ? ";
			DTOMap dtoDataLTKT = masterServices.getMapMaster(sql, new Object[] { dtoMap.getString("CIFID") });
			
			sql= "SELECT NOMOR_REKENING, max(NILAI_TRANSAKSI_RP) FROM PPATK_EXCEL_RINCI WHERE CIFID=? AND TANGGAL_TRANSAKSI =? AND DBKR=?"
				+" GROUP BY NOMOR_REKENING "
				+" FETCH FIRST 1 ROWS ONLY ";
			
			DTOMap dtNoRek= masterServices.getMapMaster(sql,  new Object[] { dtoMap.getString("CIFID"),
					sdf.format(dtoMap.getDate("TANGGAL_TX")), dtoMap.getString("DBKR") });
			

			elementTerlapor.appendChild(setElement("kepemilikan", setDataXML("1"), doc));
			elementTerlapor.appendChild(setElement("noRekening", setDataXML(dtNoRek.getString("NOMOR_REKENING")), doc));
			elementTerlapor.appendChild(setElement("gelar", setDataXML(""), doc));

			// if(!dtoDataLTKT.getString("NAMALENGKAP").isEmpty()) {
			elementTerlapor
					.appendChild(setElement("namaLengkap", setDataXML(dtoDataLTKT.getString("NAMALENGKAP")), doc));
			/*
			 * }else { elementTerlapor.appendChild(setElement("namaLengkap","",doc)); }
			 */
			elementTerlapor
					.appendChild(setElement("tempatLahir", setDataXML(dtoDataLTKT.getString("TEMPATLAHIR")), doc));
			elementTerlapor.appendChild(setElement("tglLahir", setDataXML(dtoDataLTKT.getDate("TGLLAHIR")), doc));
			elementTerlapor
					.appendChild(setElement("wargaNegara", setDataXML(dtoDataLTKT.getString("WARGANEGARA")), doc));
			elementTerlapor
					.appendChild(setElement("idNegaraAsal", setDataXML(dtoDataLTKT.getString("IDNEGARAASAL")), doc));
			elementTerlapor.appendChild(setElement("namaJalan", setDataXML(dtoDataLTKT.getString("NAMAJALAN")), doc));
			elementTerlapor.appendChild(setElement("rt", setDataXML(dtoDataLTKT.getString("RT")), doc));
			elementTerlapor.appendChild(setElement("rw", setDataXML(dtoDataLTKT.getString("RW")), doc));
			elementTerlapor
					.appendChild(setElement("idKelurahan", setDataXML(dtoDataLTKT.getString("IDKELURAHAN")), doc));
			elementTerlapor
					.appendChild(setElement("idKecamatan", setDataXML(dtoDataLTKT.getString("IDKECAMATAN")), doc));
			elementTerlapor.appendChild(setElement("kodePos", setDataXML(dtoDataLTKT.getString("KODEPOS")), doc));
			elementTerlapor.appendChild(setElement("idKotaKab", setDataXML(dtoDataLTKT.getString("IDKOTAKAB")), doc));
			idtrxProvinsi = dtoDataLTKT.getString("IDPROPINSI");
			idtrxKab = dtoDataLTKT.getString("IDKOTAKAB");
			elementTerlapor.appendChild(setElement("idPropinsi", setDataXML(dtoDataLTKT.getString("IDPROPINSI")), doc));
			elementTerlapor.appendChild(setElement("idNegara", setDataXML(dtoDataLTKT.getString("IDNEGARAASAL")), doc));
			elementTerlapor
					.appendChild(setElement("namaJalanIdentitas", setDataXML(dtoDataLTKT.getString("NAMAJALAN")), doc));
			elementTerlapor.appendChild(setElement("rtIdentitas", setDataXML(dtoDataLTKT.getString("RT")), doc));
			// elementTerlapor.appendChild(setElement("rtIdentitas",
			// setDataXML(dtoDataLTKT.getString("RWIDENTITAS")), doc));
			elementTerlapor.appendChild(setElement("rwIdentitas", setDataXML(dtoDataLTKT.getString("RW")), doc));
			elementTerlapor.appendChild(
					setElement("idKelurahanIdentitas", setDataXML(dtoDataLTKT.getString("IDKELURAHAN")), doc));
			elementTerlapor.appendChild(
					setElement("idKecamatanIdentitas", setDataXML(dtoDataLTKT.getString("IDKECAMATAN")), doc));
			elementTerlapor.appendChild(
					setElement("idPropinsiIdentitas", setDataXML(dtoDataLTKT.getString("IDPROPINSI")), doc));
			elementTerlapor
					.appendChild(setElement("kodePosIdentias", setDataXML(dtoDataLTKT.getString("IDKODEPOS")), doc));
			elementTerlapor
					.appendChild(setElement("idKotaKabIdentitas", setDataXML(dtoDataLTKT.getString("IDKOTAKAB")), doc));
			elementTerlapor.appendChild(
					setElement("idPropinsiIdentitas", setDataXML(dtoDataLTKT.getString("IDPROPINSI")), doc));
			elementTerlapor.appendChild(
					setElement("idNegaraIdentitas", setDataXML(dtoDataLTKT.getString("IDNEGARAASAL")), doc));
			elementTerlapor.appendChild(setElement("namaJalanNegaraAsal", setDataXML(""), doc));
			elementTerlapor.appendChild(setElement("kodePosNegaraAsal", setDataXML(""), doc));
			elementTerlapor.appendChild(setElement("kotaNegaraAsal", setDataXML(""), doc));

			String provinsiNeagaraAsal;
			if (dtoDataLTKT.getString("IDPROPINSI").equals("91")) {
				provinsiNeagaraAsal = "Papua";
			}
			if (dtoDataLTKT.getString("IDPROPINSI").equals("92")) {
				provinsiNeagaraAsal = "Papua Barat";
			} else {
				provinsiNeagaraAsal = "lainnya";
			}

			elementTerlapor.appendChild(setElement("propinsiNegaraAsal", setDataXML(provinsiNeagaraAsal), doc));
			elementTerlapor
					.appendChild(setElement("NegaraAsal", setDataXML(dtoDataLTKT.getString("IDNEGARAASAL")), doc));
			elementTerlapor.appendChild(setElement("ktp", setDataXML(dtoDataLTKT.getString("KTP")), doc));
			elementTerlapor.appendChild(setElement("sim", setDataXML(dtoDataLTKT.getString("SIM")), doc));
			elementTerlapor.appendChild(setElement("passport", setDataXML(""), doc));
			elementTerlapor.appendChild(setElement("kimsKitasKitap", setDataXML(""), doc));
			elementTerlapor.appendChild(setElement("buktiLain", setDataXML(dtoDataLTKT.getString("BUKTILAIN")), doc));
			elementTerlapor.appendChild(setElement("npwp", setDataXML(""), doc));
			elementTerlapor.appendChild(
					setElement("pekerjaanUtama", setDataXML(dtoDataLTKT.getString("PEKERJAANUTAMA")), doc));
			elementTerlapor.appendChild(setElement("jabatan", setDataXML(""), doc));
			elementTerlapor.appendChild(setElement("penghasilan", setDataXML(""), doc));
			elementTerlapor.appendChild(setElement("tempatKerja", setDataXML(""), doc));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setXMLKorporasi(Element elementTerlapor, Document doc, DTOMap dtoMap) {
		try {
			String sql = "SELECT * FROM PPATK_CIFCOM WHERE CIFID= ? ";
			// System.out.println(" setXMLKorporasi Cifid :" + dtoMap.getString("CIFID"));
			DTOMap dtoDataLTKT = masterServices.getMapMaster(sql, new Object[] { dtoMap.getString("CIFID") });
			
			sql= "SELECT NOMOR_REKENING, max(NILAI_TRANSAKSI_RP) FROM PPATK_EXCEL_RINCI WHERE CIFID=? AND TANGGAL_TRANSAKSI =? AND DBKR=?"
					+" GROUP BY NOMOR_REKENING"
					+" FETCH FIRST 1 ROWS ONLY ";
			DTOMap dtNoRek= masterServices.getMapMaster(sql,  new Object[] { dtoMap.getString("CIFID"),
					sdf.format(dtoMap.getDate("TANGGAL_TX")), dtoMap.getString("DBKR") });

			// elementTerlapor.appendChild(setElement("kepemilikan",
			// setDataXML(dtoDataLTKT.getString("NAMA_LENGKAP")), doc));
			elementTerlapor.appendChild(setElement("kepemilikan", setDataXML("1"), doc));
			elementTerlapor.appendChild(setElement("noRekening", setDataXML(dtNoRek.getString("NOMOR_REKENING")), doc));
			elementTerlapor
					.appendChild(setElement("namaKorporasi", setDataXML(dtoDataLTKT.getString("NAMAKORPORASI")), doc));
			elementTerlapor.appendChild(
					setElement("bidangUsahaLain", setDataXML(dtoDataLTKT.getString("BIDANGUSAHALAIN")), doc));
			elementTerlapor.appendChild(setElement("namaJalan", setDataXML(dtoDataLTKT.getString("NAMAJALAN")), doc));
			elementTerlapor.appendChild(setElement("rt", setDataXML(dtoDataLTKT.getString("RT")), doc));
			elementTerlapor.appendChild(setElement("rw", setDataXML(dtoDataLTKT.getString("RW")), doc));
			elementTerlapor
					.appendChild(setElement("idKelurahan", setDataXML(dtoDataLTKT.getString("IDKELURAHAN")), doc));
			elementTerlapor
					.appendChild(setElement("idKecamatan", setDataXML(dtoDataLTKT.getString("IDKECAMATAN")), doc));
			elementTerlapor.appendChild(setElement("kodePos", setDataXML(dtoDataLTKT.getString("KODEPOS")), doc));
			idtrxProvinsi = dtoDataLTKT.getString("IDPROPINSI");
			idtrxKab = dtoDataLTKT.getString("IDKOTAKAB");
			elementTerlapor.appendChild(setElement("idKotaKab", setDataXML(dtoDataLTKT.getString("IDKOTAKAB")), doc));
			elementTerlapor.appendChild(setElement("idPropinsi", setDataXML(dtoDataLTKT.getString("IDPROPINSI")), doc));
			elementTerlapor.appendChild(setElement("idNegara", setDataXML(dtoDataLTKT.getString("IDNEGARA")), doc));
			elementTerlapor.appendChild(setElement("npwp", setDataXML(dtoDataLTKT.getString("NPWP")), doc));
			// elementTerlapor.appendChild(setElement("bidangUsaha",
			// setDataXML(dtoDataLTKT.getString("BIDANGUSAHA")), doc));
			elementTerlapor.appendChild(setElement("bidangUsaha", setDataXML("22"), doc));
			elementTerlapor
					.appendChild(setElement("bentukBadan", setDataXML(dtoDataLTKT.getString("BENTUKBADAN")), doc));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		generate();
		//readFileName();

	}
	
	private void readFileName() {
		
		String path = env.getProperty("PATH_XML_APUPPT").toString() + File.separatorChar;
		String pathFileTxt = path + "files.txt";	
		
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		File file = new File(pathFileTxt);
        FileWriter fr = null;
        try {
            fr = new FileWriter(file);
            
        } catch (IOException e) {
            e.printStackTrace();
        }

		for (int i = 0; i < listOfFiles.length; i++) {
		  if (listOfFiles[i].isFile()) {
			
		try {
				fr.write(listOfFiles[i].getName());
				fr.write("\r\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    System.out.println("File " + listOfFiles[i].getName());
		  } else if (listOfFiles[i].isDirectory()) {
		    System.out.println("Directory " + listOfFiles[i].getName());
		  }
		}
		try {
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		
	}

}
