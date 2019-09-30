package com.tianyi.datacenter.common.excel;

import com.github.liaochong.myexcel.core.DefaultExcelReader;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public class ExcelUtil {
	public static final String XLSX = ".xlsx";
	public static final String XLS = ".xls";


/*	public static <T> AnalysisEventListener<T> importExcel(MultipartFile excel, Class<? extends BaseRowModel> modelClass, AnalysisEventListener<T> listener) throws IOException {
		 
		ExcelReader excelReader = null;
		if (excel.getOriginalFilename().endsWith(ExcelUtil.XLSX)) {
			excelReader = new ExcelReader(excel.getInputStream(), ExcelTypeEnum.XLSX, listener, true);
		} else if (excel.getOriginalFilename().endsWith(ExcelUtil.XLS)) {
			excelReader = new ExcelReader(new BufferedInputStream(excel.getInputStream()), ExcelTypeEnum.XLS, listener, true);
		}
		excelReader.read(new Sheet(1, 1, modelClass));
		return listener;
	}*/

	/**
	 *  读取数据
	 * @param excel
	 * @param modelClass
	 * @param <T>
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public static  <T> List<T> readExcel(MultipartFile excel, Class<T> modelClass) throws IOException, Exception {
		return DefaultExcelReader.of(modelClass).sheet(0).rowFilter(row->row.getRowNum()>0).read(excel.getInputStream());
	}

}
