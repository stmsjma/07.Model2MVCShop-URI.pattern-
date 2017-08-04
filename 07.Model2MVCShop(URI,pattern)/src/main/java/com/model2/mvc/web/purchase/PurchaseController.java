package com.model2.mvc.web.purchase;

import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.model2.mvc.common.CommonUtil;
import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Inventory;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.inventory.InventoryService;
import com.model2.mvc.service.inventory.impl.InventoryServiceImpl;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;
import com.model2.mvc.service.purchase.PurchaseService;
import com.model2.mvc.service.user.UserService;


//==> 상품관리  Controller
@Controller
@RequestMapping("/purchase/*")
public class PurchaseController {
	
	///Field
	@Autowired
	@Qualifier("purchaseServiceImpl")
	private PurchaseService purchaseService;
	
	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;

	@Autowired
	@Qualifier("inventoryServiceImpl")
	private InventoryService inventoryService;
	
	//setter Method 구현 않음
		
	public PurchaseController(){
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
	
	
	//@RequestMapping("/addPurchaseView.do")
	@RequestMapping( value="addPurchase", method=RequestMethod.GET)
	public String addPurchase(@ModelAttribute("product") Product product,
								  HttpServletRequest request,
								  Model model ) 
								  throws Exception {
		
		String productNo=request.getParameter("prod_no");
		
		System.out.println(" AddPurchaseViewAction ==>> productno :::" + productNo );
		
		product = productService.getProduct(Integer.parseInt(productNo));
		
		System.out.println(" AddPurchaseViewAction ==>> product :::" + product );
		
		model.addAttribute("product", product);
		
			
		return "forward:/purchase/addPurchaseView.jsp";
	}
	
	//@RequestMapping("/addPurchase.do")
	@RequestMapping (value ="addPurchase", method=RequestMethod.POST)
	public String addPurchase( @ModelAttribute("purchase")  Purchase  purchase, 
							   @ModelAttribute("product")   Product   product,
							   @ModelAttribute("inventory") Inventory inventory,  
							   HttpSession session,
							   HttpServletRequest request,
							   Search search, Model model) throws Exception {

		User user=(User)session.getAttribute("user");
		String transCode = "002";
		String productNo=request.getParameter("prodNo");
		Date date = null;
		
		System.out.println("PurchaseControlled user :: " + user + "prodNo :: " + productNo);

		product=productService.getProduct(Integer.parseInt(productNo));
		
		model.addAttribute("product",product);
		
		System.out.println("/addProduct.do");
		
		
		purchase.setPurchaseProd(product);
		purchase.setBuyer(user);
		purchase.setDivyAddr(request.getParameter("receiverAddr"));
		
		String strDate = request.getParameter("divyDate");
		strDate = strDate.replaceAll("-", "");
		purchase.setDivyAddr(strDate);
		int request_qty = (Integer.parseInt(request.getParameter("quanity")));
		
		
		/*
		purchase.setDivyDate(sqlDate);
		purchase.setDivyRequest(request.getParameter("receiverRequest"));
		purchase.setPaymentOption(request.getParameter("paymentOption"));
		purchase.setReceiverName(request.getParameter("receiverName"));
		purchase.setReceiverPhone(request.getParameter("receiverPhone"));
		*/
		product.setQuanity(Integer.parseInt(request.getParameter("quanity")));
		purchase.setPurchaseProd(product);
		purchase.setTranCode(transCode);  
		
		
		purchaseService.addPurchase(purchase);
				
		inventory.setProduct(product);
		inventory.setOutDate(strDate);
		
		Map<String,Object> map = inventoryService.extractInventory(search, inventory);
		System.out.println("ProductControl map show ==>> " + map);
		
		List<Object> list = (List<Object>)map.get("list");
		
		for (int i = 0 ; i < list.size() ; i ++ ) {
			System.out.println("list.get[i] contents ==>> " + list.get(i).toString());
		}
		
		int inventory_qty = inventory.getInventoryQuanity();
		System.out.println("inventory_no" + inventory.getInventoryNo());
		
		
		System.out.println("inventory.getInventoryQuanity() ::" + inventory.getInventoryQuanity());
		
		inventory_qty = inventory_qty - request_qty;
		System.out.println("inventory_qty ::" + inventory_qty);
		
		System.out.println("AddPurchaseAction ==>> inventory_qty :: " + inventory_qty);
		
		inventory.setInventoryQuanity(inventory_qty);
		inventory.setOutQuanity(request_qty);
		inventory.setProduct(product);
		
		inventoryService.updateInventory(inventory);

		//Business Logic

		return "forward:/purchase/addPurchase.jsp";
		
	}
	
	//@RequestMapping("/getPurchase.do")
	@RequestMapping(value = "getPurchase", method=RequestMethod.GET)
	public String getProduct( @RequestParam(value="tranNo", required=true)  
									 Integer tranNo, 
									 Model model ) throws Exception {
		
		System.out.println("/getPurchase    GET::: ");
		//Business Logic
		
		Purchase purchase = purchaseService.getPurchase(tranNo);
		// Model 과 View 연결
		model.addAttribute("purchase", purchase);
		
	
		return "forward:/purchase/showPurchaseDetail.jsp";
	}
		
	
	//@RequestMapping("/updatePurchaseView.do")
	@RequestMapping(value = "updatePurchase", method=RequestMethod.GET)
	public String updatePurchase (@RequestParam(value="tranNo", required=true)  
										Integer tranNo,
										Model model ) throws Exception{

		System.out.println("/updatePurchase ::: GET ");
		//Business Logic
		
		Purchase purchase = purchaseService.getPurchase(tranNo);
		// Model 과 View 연결
		model.addAttribute("purchase", purchase);

		return "forward:/purchase/updatePurchaseDetail.jsp";
	}
	
	//@RequestMapping("/updatePurchase.do")
	@RequestMapping(value="updatePurchase", method=RequestMethod.POST)
	public String updatePurchase(   @ModelAttribute("purchase") Purchase purchase, 
									Model model ,
									HttpServletRequest request,
					 			    @RequestParam(value="tranNo", required=true)  
									String tranNo) 
									throws Exception{

	
		System.out.println("/updatePurchase  ::: POST");
				
		// int transactionNo = Integer.parseInt(request.getParameter("tranNo"));
		// System.out.println("transNo :: " + transactionNo);
		
		String strDate = request.getParameter("divyDate");
		System.out.println("strDate ===>>> " + strDate);
		
		
		strDate = strDate.replaceAll("-", "");
		purchase.setDivyAddr(strDate);
		purchase.setTranNo(Integer.parseInt(tranNo));

		System.out.println("Purchase =====>>>  " + purchase);
		
		model.addAttribute("tranNo",tranNo);
		//Business Logic
		purchaseService.updatePurchase(purchase);

		return "redirect:/purchase/getPurchase?tranNo="+tranNo;
	}

	//@RequestMapping("/updateTranCode.do")
	@RequestMapping(value = "updateTranCode", method=RequestMethod.GET)
	public String updateTranCode(   @ModelAttribute("purchase") Purchase purchase, 
									@RequestParam(value="tranCode", required=true)
									String tranCode,
									@RequestParam(value="prodNo", required=true)
									Integer prodNo,
									Model model , HttpServletRequest request) 
									throws Exception{

		System.out.println("/updateTranCode.do");
		System.out.println("prodNo :: " + prodNo);
								
		//Business Logic
		Product product = new Product();
		product.setProdNo(prodNo);
		purchase.setPurchaseProd(product);
		
				
		purchaseService.updateTranCode(purchase);
						
		return "/product/listProduct?menu=manage";
	}
	
	//@RequestMapping("/listPurchase.do")
	@RequestMapping( value="listPurchase")
	public String listPurchase( @ModelAttribute("search") Search search, 
								@ModelAttribute("user")User user, 
								Model model , HttpSession session) 
							    throws Exception{
		
		System.out.println("/listPurchase :: GET");
		
		if(search.getCurrentPage() ==0 ){
			search.setCurrentPage(1);
		}
		search.setPageSize(pageSize);
		
		String buyerId=((User)session.getAttribute("user")).getUserId();
		System.out.println("/listPurchase.do +++ buyerId ::: " + buyerId);
		
		
		
		// Business logic 수행
		Map<String , Object> map=purchaseService.getPurchaseList(search, buyerId);
			
		Page resultPage = new Page( search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		System.out.println(resultPage);
		
		// Model 과 View 연결
		model.addAttribute("list", map.get("list"));
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("search", search);
		
		return "forward:/purchase/listPurchase.jsp";
	}
	
	//@RequestMapping("/updateTranCodeByProd.do")
	@RequestMapping(value="updateTranCodeByProd", method=RequestMethod.GET)
	public String updateTranCodeByProd(@ModelAttribute("purchase") Purchase purchase, 
									@ModelAttribute("product") Product product,
									@RequestParam(value="tranCode", required=true)  
									String tranCode,
									@RequestParam(value="prodNo", required=true)  
									Integer prodNo,
									Model model , HttpServletRequest request) 
									throws Exception{
		
		purchase.setTranCode(tranCode);
		product.setProdNo(prodNo);
		purchase.setPurchaseProd(product);
		
		
		System.out.println("updateTranCodeByProd.do purchase :: " + purchase);
				
		System.out.println("updateTranCodeByProd Biz Logic :: started... ==>>>");
		// Business logic 수행
		
		purchaseService.updateTranCode(purchase);
		
		model.addAttribute("purchase",purchase);
		
		return "/product/listProduct?menu=manage";
	}

}