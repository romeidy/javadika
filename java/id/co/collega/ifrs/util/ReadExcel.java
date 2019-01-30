	/**
 * 
 */
package id.co.collega.ifrs.util;

import java.io.File;
import java.io.IOException;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ReadExcel {

	private String inputFile;

	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	public void read() throws IOException  {
		File inputWorkbook = new File(inputFile);
		Workbook w;
		try {
			w = Workbook.getWorkbook(inputWorkbook);
			// Get the first sheet
			Sheet sheet = w.getSheet(0);
			// Loop over first 10 column and lines

//			for (int j = 0; j < sheet.getColumns(); j++) {
//				for (int i = 0; i < sheet.getRows(); i++) {
//					Cell cell = sheet.getCell(j, i);
//					CellType type = cell.getType();
//					if (cell.getType() == CellType.LABEL) {
//						System.out.println("I got a label "
//								+ cell.getContents());
//					}
//
//					if (cell.getType() == CellType.NUMBER) {
//						System.out.println("I got a number "
//								+ cell.getContents());
//					}
//
//				}
//			}
			
//			Cell cell = sheet.getCell(1, 12);
//			CellType type = cell.getType();
//			if(type==CellType.LABEL){
//				System.out.println("labelnya : "+cell.getContents());
//			}
//			 cell = sheet.getCell(4, 12);
//			 type = cell.getType();
//			if(type==CellType.NUMBER_FORMULA){
//				System.out.println("nomornya : "+cell.getContents());
//			}
			
			Cell cell = getCell(sheet,"A27174");
			CellType type = cell.getType();
			System.out.println("isi cell "+cell.getContents());
			System.out.println("type cell "+type.toString());
		} catch (BiffException e) {
			e.printStackTrace();
		}
	}

	private Cell getCell(Sheet sheet,String sel) {
		//wonx baca cell berdasarkan sel
		int col=getKolom(sel);
		if(col==-1)
			return null;
		
		int row=getRow(sel);
		if(row==-1)
			return null;
			
			
		return sheet.getCell(col, row);
	}

	private int getRow(String sel) {
		int indexBaris=0;
		int baris=-1;
		for(int i=0;i<sel.length();i++){
			if(isNumber(sel.charAt(i))){
				indexBaris=i;
				break;
				
			}
		}
		
		if(indexBaris!=0){
			//index mulai dari 0
			baris=Integer.parseInt(sel.substring(indexBaris))-1;
		}
		return baris;
	}

	private int getKolom(String sel) {
		int max=26;
		int jmlKarKolom=0;
		String charKolom="";
		int kolom=-1;
		for(int i=0;i<sel.length();i++){
			if(!isNumber(sel.charAt(i))){
				jmlKarKolom++;
				charKolom+=String.valueOf(sel.charAt(i));
			}
		}
		if(jmlKarKolom>1){
			int f=(int)charKolom.charAt(0);
			int g=(int)charKolom.charAt(0);
			f=f-64;
			g=g-64;
			kolom=(max*f)+g;
		}else if(jmlKarKolom==1){
			int x=(int)charKolom.charAt(0);
			kolom=x-65;
		}
		return kolom;
	}

	private boolean isNumber(char c) {
		try{
			Integer.parseInt(String.valueOf(c));
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}

	public static void main(String[] args) throws IOException {
		ReadExcel test = new ReadExcel();
//		test.setInputFile("c:/temp/lars.xls");
		test.setInputFile(".\\JAKCLOTH2014.xls");
		test.read();
	}

}