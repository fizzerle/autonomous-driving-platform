package tuwien.dse.apigateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FischController {

    @Autowired
    private FischService fischService;

    @GetMapping("/fisch")
    public String getFisch() {
        return fischService.getFisch();
    }

    @GetMapping("/dead")
    public String getDeadFisch() {
        return fischService.getDeadFisch();
    }

    @GetMapping("/igel/*")
    public String getigel(HttpRequest req){
        return req.getURI().toString();
    }

    @PostMapping("/igel/*")
    public String postigel(HttpRequest req){
        return req.getURI().toString();
    }

}