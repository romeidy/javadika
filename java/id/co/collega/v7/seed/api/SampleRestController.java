package id.co.collega.v7.seed.api;


import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SampleRestController {

    /*@RequestMapping(value = "/hello/{name}", method = RequestMethod.GET)
    public Map<String,Object> hello(@PathVariable String name){
        return Collections.<String,Object>singletonMap("message","Hello "+name);
    }*/

}
