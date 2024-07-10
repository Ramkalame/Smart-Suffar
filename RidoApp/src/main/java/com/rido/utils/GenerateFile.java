package com.rido.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import com.rido.entityDTO.DriverPaymentDto;

@Component
public class GenerateFile {
	
	public static String[] HEADER = {
			"BeneficiaryFundAccountID",
			"PayoutAmount",
			"PayoutMode",
			"PayoutNarration",
			"Notes",
			"BeneficiaryName",
			"PhoneNumber",
			"Email",
			"PayoutReferenceID"
			
	};
	public static String SHEET_NAME = "driver_payment-details";
	
	public static ByteArrayInputStream dataToExcel(List<DriverPaymentDto> driverPaymentlist) throws IOException {
		
		//first we have to create work book there we work on that file
		Workbook workBook = new HSSFWorkbook();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		
		try {
			
			//now we have to create sheet
			Sheet sheet =  workBook.createSheet(SHEET_NAME);
//			create row for the sheet
			Row row = sheet.createRow(0);
			
//			add value in the header means row 1
			for(int i =0; i<HEADER.length; i++) {
				Cell cell = row.createCell(i);
				cell.setCellValue(HEADER[i]);
			}
			
//			add value in the each column
			int rowIndex=1;
			for(DriverPaymentDto paymentDetail: driverPaymentlist) {
				Row dataRow = sheet.createRow(rowIndex);
				rowIndex++;
				dataRow.createCell(0).setCellValue(paymentDetail.getAccountNo());
				dataRow.createCell(1).setCellValue(paymentDetail.getPayableAmount());
				dataRow.createCell(2).setCellValue(paymentDetail.getPaymentMode());
				dataRow.createCell(3).setCellValue(paymentDetail.getPayOutNarration());
				dataRow.createCell(4).setCellValue(paymentDetail.getNotes());
				dataRow.createCell(5).setCellValue(paymentDetail.getBeneficiaryName());
				dataRow.createCell(6).setCellValue(paymentDetail.getPnoneNo());
				dataRow.createCell(7).setCellValue(paymentDetail.getEmail());
				dataRow.createCell(8).setCellValue(paymentDetail.getInvoiceNo());	
			}
			
			workBook.write(out);
			return new ByteArrayInputStream(out.toByteArray());
			
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("faild toimport data excel");
			return null;
		}finally {
			workBook.close();
			out.close();
		}
		
	}

}
