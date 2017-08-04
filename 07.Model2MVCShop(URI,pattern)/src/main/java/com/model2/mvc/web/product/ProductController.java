package com.model2.mvc.web.product;


import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.management.modelmbean.RequiredModelMBean;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.ProductService;



//==> 상품관리  Controller
@Controller
@RequestMapping("/product/*")
public class ProductController {
	
	///Field
	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;
	//setter Method 구현 않음
		
	public ProductController(){
		System.out.println(this.getClass());
	}
	
	//==> classpath:config/common.properties  ,  classpath:config/commonservice.xml 참조 할것
	//==> 아래의 두개를 주석을 풀어 의미를 확인 할것
	@Value("#{commonProperties['pageUnit']}")
	//@Value("#{commonProperties['pageUnit'] ?: 3}")
	int pageUnit;
	
	@Value("#{commonProperties['pageSize']}")
	//@Value("#{commonProperties['pageSize'] ?: 2}")
	int pageSize;
	
	
	//@RequestMapping("/addProductView.do")
	@RequestMapping( value="addProduct", method=RequestMethod.GET)
	public ModelAndView addProductView() throws Exception {
		
		
		ModelAndView modelAndView = new ModelAndView();
		
		System.out.println("/addProduct : GET");
		
		modelAndView.setViewName("redirect:/product/addProductView.jsp");
	
		return modelAndView;
	}
	
	//@RequestMapping("/addProduct.do")
	@RequestMapping (value ="addProduct", method=RequestMethod.POST)
	public ModelAndView addProduct( @RequestParam("file")MultipartFile file,
									@ModelAttribute("product") Product product,
									HttpServletRequest request,									
									Model model
								  ) throws Exception {
		
		/**
		 * Upload single file using Spring Controller
		 */
		
		System.out.println("addProduct Controller Start ===>>> ");
		
		ModelAndView modelAndView = new ModelAndView();
		
		String tempDir =
			   "C:\\Users\\jeung\\git\\07.Model2MVCShop(URI,pattern)\\07.Model2MVCShop(URI,pattern)\\WebContent\\images\\uploadFiles\\";
		
		String fileName = file.getOriginalFilename();
		
		File toFile=new File(tempDir+file.getOriginalFilename());
		file.transferTo(toFile);
		
		String strDate = product.getManuDate();
		strDate = strDate.replaceAll("-", "");
		
		product.setManuDate(strDate);
		product.setFileName(fileName);
			
		productService.addProduct(product);
		modelAndView.addObject("product",product);
       
		/*
		boolean isSuccess = false;
		
		String uploadPath = "C:\\Users\\jeung\\git\\07.Model2MVCShop(URI,pattern)\\07.Model2MVCShop(URI,pattern)\\WebContent\\images\\uploadFiles\\";
		
		File dir = new File(uploadPath);

		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
		
		Iterator<String> iter = mrequest.getFileNames();
		while(iter.hasNext()) {
			String uploadFileName = iter.next();
			
			MultipartFile mFile = mrequest.getFile(uploadFileName);
			String originalFileName = mFile.getOriginalFilename();
			String saveFileName = originalFileName;
			
			if(saveFileName != null && !saveFileName.equals("")) {
				if(new File(uploadPath + saveFileName).exists()) {
					saveFileName = saveFileName + "_" + System.currentTimeMillis();
				}
				
				try {
					mFile.transferTo(new File(uploadPath + saveFileName));
					isSuccess = true;				
				} catch (IllegalStateException e) {
					e.printStackTrace();
					isSuccess = false;
				} catch (IOException e) {
					e.printStackTrace();
					isSuccess = false;
				}
			} // if end
		} // while end
	
		
		ModelAndView modelAndView = new ModelAndView();
		
		String strDate = product.getManuDate();
		strDate = strDate.replaceAll("-", "");
		product.setManuDate(strDate);
		
		productService.addProduct(product);
		*/
		/*
		
		String mandate1 = "";
		String mandate2 = "";
		String mandate3 = "";
		
		mandate1 =  product.getManuDate().substring(0,4);
		mandate2 =  product.getManuDate().substring(5,7);
		mandate3 =  product.getManuDate().substring(8,10);
		
		mandate1 = mandate1 + mandate2 + mandate3;
				
		product.setManuDate(mandate1);
		
		System.out.println("/addProduct.do");
		
		//productService.addProduct(product); */
		
		//******************************************************
		//** File Upload Logic Implement using 2010 coding style 
		//**
		//******************************************************
		/*
		if(FileUpload.isMultipartContent(request)) {
			String tempDir =
			"C:\\Users\\jeung\\git\\07.Model2MVCShop(URI,pattern)\\07.Model2MVCShop(URI,pattern)\\WebContent\\images\\uploadFiles\\";
			
			DiskFileUpload fileUpload = new DiskFileUpload();
			fileUpload.setRepositoryPath(tempDir);
			fileUpload.setSizeMax(1024*1024*10);
			fileUpload.setSizeThreshold(1024*100);
		
			if(request.getContentLength() < fileUpload.getSizeMax()) {
				StringTokenizer token = null;
				
				List fileItemList = fileUpload.parseRequest(request);
				int Size = fileItemList.size();
				for (int i = 0; i < Size ; i++) {
					FileItem fileItem = (FileItem) fileItemList.get(i);
					if (fileItem.isFormField()) {
						if(fileItem.getFieldName().equals("manuDate")) {
							token = new StringTokenizer(fileItem.getString("euc-kr"),"-");
							String manuDate = token.nextToken() + token.nextToken() + token.nextToken();
							product.setManuDate(manuDate);
						}
						else if(fileItem.getFieldName().equals("prodNo"))
							product.setProdNo(Integer.parseInt(fileItem.getString("euc-kr")));
						else if(fileItem.getFieldName().equals("prodName"))
							product.setProdName(fileItem.getString("euc-kr"));
						else if(fileItem.getFieldName().equals("prodDetail"))
							product.setProdDetail(fileItem.getString("euc-kr"));
						else if(fileItem.getFieldName().equals("price"))
							product.setPrice(Integer.parseInt(fileItem.getString("euc-kr")));
						else if(fileItem.getFieldName().equals("wareHouseCode"))
							product.setWareHouseCode(fileItem.getString("euc-kr"));
						else if(fileItem.getFieldName().equals("quanity"))
							product.setQuanity(Integer.parseInt(fileItem.getString("euc-kr")));
				}else {
					
					if (fileItem.getSize() > 0) {
						int idx = fileItem.getName().lastIndexOf("\\");
						if (idx == -1) {
							idx = fileItem.getName().lastIndexOf("/");
						}
						String fileName = fileItem.getName().substring(idx+1);
						product.setFileName(fileName);					
						try {
							File uploadFile = new File(tempDir,fileName);
							fileItem.write(uploadFile);
						} catch (IOException e) {
							System.out.println(e);
						}
					}
					else {
						product.setFileName("../../images/empty.GIF");
					}
				} //else
			} //for
			
		productService.addProduct(product);
		
		model.addAttribute("product",product);
		
		} else {
			int overSize = (request.getContentLength()  / 1000000);
			System.out.println("<script>alert('파일이 크기는 1MB까지 입니다. 올리신 파일 용량은"
					+ overSize + "MB입니다.");
			System.out.println("history.back(); </script>");
		}
	}	
	else {
		System.out.println("인코딩 타입이 multipart/form-data 가 아닙니다. ");
	}
	*/
		
		modelAndView.setViewName("/product/registerProductView.jsp");
		//Business Logic

		return modelAndView;
	}
	
	//@RequestMapping("/getProduct.do")
	@RequestMapping( value="getProduct", method=RequestMethod.GET)
	public ModelAndView getUser( @RequestParam(value="menu", required=true)  String menu, 
								 @RequestParam(value="prodNo", required=true) Integer prodNo , 
								 Model model ) throws Exception {
		
		ModelAndView modelAndView = new ModelAndView();
		
		System.out.println("/getProduct  : GET");
		//Business Logic
		
		Product product = productService.getProduct(prodNo);
		// Model 과 View 연결
		model.addAttribute("product", product);
		
		if (menu.equals("managed")) {
			System.out.println("updateProduct.jsp called");
			System.out.println(product+"겟프로덕액션");
			modelAndView.setViewName("/product/updateProduct.jsp");
			
			return modelAndView;
		} else {
			System.out.println(product+"겟프로덕액션");
			modelAndView.setViewName("/product/readProduct.jsp");
			
			return modelAndView;
		}
		
	}
	
	//@RequestMapping("/updateProductView.do")
	@RequestMapping( value="updateProduct", method=RequestMethod.GET)
	public ModelAndView updateProductView( @RequestParam("prodNo") Integer prodNo , Model model ) throws Exception{

		System.out.println("/updateProductView.do");
		//Business Logic
		ModelAndView modelAndView = new ModelAndView();
		
		Product product = productService.getProduct(prodNo);
		// Model 과 View 연결
		model.addAttribute("product", product);
		
		modelAndView.setViewName("/product/updateProductView.jsp");
		
		return modelAndView;
	}
	
	//@RequestMapping("/updateProduct.do")
	@RequestMapping( value="updateProduct", method=RequestMethod.POST)
	public ModelAndView updateProduct( @ModelAttribute("product") Product product , Model model , HttpSession session) throws Exception{

		System.out.println("/updateProduct.do");
		ModelAndView modelAndView = new ModelAndView();
		
		//Business Logic
		productService.updateProduct(product);
		
		modelAndView.setViewName("/updateProductView");
				
		return modelAndView;
	}
	
	//@RequestMapping("/listProduct.do")
	@RequestMapping( value="listProduct")
	public ModelAndView listProduct( @ModelAttribute("search") Search search, 
								  Model model , HttpServletRequest request) 
								  throws Exception{
		
		System.out.println("/listProduct  GET ");
		
		ModelAndView modelAndView = new ModelAndView();
		
		if(search.getCurrentPage() ==0 ){			search.setCurrentPage(1);
		}
		search.setPageSize(pageSize);
		
		// Business logic 수행
		Map<String , Object> map=productService.getProductList(search);
		
		Page resultPage = new Page( search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		System.out.println(resultPage);
		
		
		
		// Model 과 View 연결
		model.addAttribute("list", map.get("list"));
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("search", search);
		
		System.out.println("list show ::"  + map.get("list"));
		
		
		if(request.getParameter("menu").equals("search")) {
			System.out.println("listProduct.jsp called");
			modelAndView.setViewName("/product/listProduct.jsp"); 
			return modelAndView;
		}
		else  { 
			System.out.println("manageProduct.jsp called");
			modelAndView.setViewName("/product/manageProduct.jsp");
			return modelAndView;
	   }
	}
}