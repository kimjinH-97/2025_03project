package com.project03.controller.manufacturing;

import com.project03.domain.Order;
import com.project03.dto.orders.OrderPageRequestDTO;
import com.project03.dto.orders.OrderPageResponseDTO;
import com.project03.repository.manufacturing.OrderRepository;
import com.project03.service.manufacturing.OrderService;
import com.project03.service.manufacturing.ProductService;
import com.project03.service.manufacturing.RouteService;
import com.project03.service.manufacturing.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @Autowired
    @Qualifier("manufacturingRouteService")
    private RouteService routeService;

    private final ProductService productService;
    private final WarehouseService warehouseService;


    @Autowired

    public OrderController(OrderService orderService, @Qualifier("manufacturingRouteService")RouteService routeService, ProductService productService, WarehouseService warehouseService, OrderRepository orderRepository) {
        this.warehouseService = warehouseService;
        this.orderService = orderService;
        this.routeService = routeService;
        this.productService = productService;
        this.orderRepository = orderRepository;
    }

    // 1. 주문 리스트 보기(order/list)
//    @GetMapping("/list")
//    public String listOrders(
//            @RequestParam(required = false) String type,
//            @RequestParam(required = false) String keyword,
//            Model model) {
//
//        List<Order> orders;
//
//        if (type != null && keyword != null && !keyword.isBlank()) {
//            orders = orderService.searchOrders(type, keyword);
//        } else {
//            orders = orderService.getAllOrders();
//        }
//
//        model.addAttribute("orders", orders);
//        model.addAttribute("type", type);
//        model.addAttribute("keyword", keyword);
//        return "orders/list";
//    }
    @GetMapping("/list")
    public String listOrders(OrderPageRequestDTO requestDTO, Model model) {
        OrderPageResponseDTO<Order> responseDTO = orderService.getList(requestDTO);

        // orders 리스트와 responseDTO 둘 다 넘김
        model.addAttribute("orders", responseDTO.getDtoList());
        model.addAttribute("responseDTO", responseDTO);
        model.addAttribute("type", requestDTO.getType());
        model.addAttribute("keyword", requestDTO.getKeyword());

        return "orders/list";
    }




    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("order", new Order());
        model.addAttribute("warehouses", warehouseService.getUnorderedWarehouses());
        model.addAttribute("routes", routeService.getAllRoutes());
        return "orders/register";
    }



    // 3. 주문 등록 처리
    @PostMapping("/register")
    public String registerOrder(Order order) {
        order.setUpdatedAt(LocalDateTime.now());

        if (order.getStatus() == null || order.getStatus().isEmpty()) {
            order.setStatus("준비중");
        }

        //  주문 등록과 동시에 해당 창고 상태 변경
        Long warehouseId = order.getWarehouse().getId();
        if (warehouseId != null) {
            warehouseService.markAsOrdered(warehouseId); // ← 이걸 WarehouseService에 만들어주자
        }

        orderRepository.save(order);
        return "redirect:/orders/list";
    }
    //  4. 주문 상세 보기
    @GetMapping("/{id}/detail")
    public String showOrderDetail(@PathVariable("id") Long id, Model model) {
        Order order = orderService.findById(id);
        if (order == null) {
            return "redirect:/orders/list"; // 없으면 리스트로
        }
        model.addAttribute("order", order);
        return "orders/detail"; // templates/orders/detail.html
    }
}
