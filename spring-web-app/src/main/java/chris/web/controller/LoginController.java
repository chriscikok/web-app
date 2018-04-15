package chris.web.controller;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Controller
public class LoginController {
	
	protected Logger logger = LoggerFactory.getLogger(LoginController.class.getName());
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
    public String landing() {
        return "landing";
    }
	
	@RequestMapping("/unauthenticated")
	public String unauthenticated() {
	  return "redirect:/?error=true";
	}
	
	@RequestMapping("/user")
	@ResponseBody
	public Principal user(Principal principal) {
		logger.debug("user:" + principal);
		if (principal == null) throw new NoPrincipalException();
		return principal;
	}
	
	
	@ResponseStatus(value=HttpStatus.UNAUTHORIZED, reason="Not yet logon or session expired")
	public class NoPrincipalException extends RuntimeException {

		private static final long serialVersionUID = 1L;
		//
	}
	
	@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Resource not found")
	public class ResourceNotFoundException extends RuntimeException {

		private static final long serialVersionUID = 1L;
		//
	}
	
	@RequestMapping("/accounts")
	@ResponseBody
	public Object accounts(String access_token) {
		logger.debug("access_token: " + access_token);
		RestTemplate restTemplate = new RestTemplate();
		String accountURL = "http://mac.chris.com:2222/accounts";
		ResponseEntity<String> response = null; 
		try {
			response = restTemplate.getForEntity(accountURL + "?access_token=" + access_token, String.class);
			//response = restTemplate.getForObject(accountURL + "?access_token=" + access_token, String.class);
		} catch (HttpClientErrorException e) {
			if (e.getRawStatusCode()==404) throw new ResourceNotFoundException();
			if (e.getRawStatusCode()==401) throw new NoPrincipalException();
		}
		return response;
	}

}
