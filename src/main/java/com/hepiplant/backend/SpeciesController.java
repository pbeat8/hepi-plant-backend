package com.hepiplant.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/species")
public class SpeciesController {

    @GetMapping
    public String someMethod(){
        return "hello";
    }

    @GetMapping(path = "/ola")
    public String someOtherMethod(){
        return "Welcome Olas!!! :)";
    }

    @GetMapping (path = "/{name}")
    public String nameMethod(@PathVariable String name){
        return "How are you, " + name + "?";
    }

}
