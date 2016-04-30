package mk.finki.ranggo.webapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import mk.finki.ranggo.webapp.model.Content;
import mk.finki.ranggo.webapp.model.Person;
import mk.finki.ranggo.webapp.service.ContentsSelector;

@Controller
public class IndexController {
	
	@Autowired
	private ContentsSelector selector;
	
	@RequestMapping(value={"", "/"}, method=RequestMethod.GET)
	public ModelAndView index() {
		return new ModelAndView("index");
	}
	
	@RequestMapping(value={"/ratings/{id}"}, method=RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Content> getRatingsForPerson(@PathVariable("id") String id){
		return selector.getRatingsForPerson(id);
	}
	
	@RequestMapping(value={"/latest-news"}, method=RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Content> getLatestNews(){
		return selector.getLatestNews();
	}
	
	//currently used for debug purposes
	
	@RequestMapping(value={"/persons"}, method=RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Person> getAllPersons(){
		return selector.getAllPersons();
	}
	
	@RequestMapping(value={"/contents"}, method=RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Content> getAllContents(){
		return selector.getAllContents();
	}
}