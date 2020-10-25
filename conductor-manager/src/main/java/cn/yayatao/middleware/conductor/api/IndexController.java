package cn.yayatao.middleware.conductor.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class IndexController {
    @GetMapping("")
    String index() {
        return "conductor manager";
    }
}
