package mk.finki.ranggo.webapp.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import mk.finki.ranggo.webapp.model.Content;
import mk.finki.ranggo.webapp.model.Person;
import mk.finki.ranggo.webapp.model.Source;
import mk.finki.ranggo.webapp.service.ContentsSelector;

@CrossOrigin(origins = "http://localhost:8000")
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
	
	@RequestMapping(value={"/averageRatings/{id}"}, method=RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Double> getAverageRatingsForPerson(@PathVariable("id") String id){
		return selector.getAverageRatingForPerson(id);
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
	
	//used for testing purposes
	@RequestMapping(value={"/top5/persons"}, method=RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Person> getTop5People(){
		return selector.getTop5People();
	}
	
	@RequestMapping(value={"/contents"}, method=RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Content> getAllContents(){
		return selector.getAllContents();
	}
	
	@RequestMapping(value={"/categories"}, method=RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<String> getCategories(){
		return selector.getCategories();
	}
	
	@RequestMapping(value={"/persons/{id}"}, method=RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Person findByID(@PathVariable String id){
		return selector.findByID(id);
	}
	
	@RequestMapping(value={"/sources"}, method=RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Source> getSources(){
		List<Source> sources = new ArrayList<Source>();
		
		sources.add(new Source("mk", "Дневник","http://www.dnevnik.mk/"));
		sources.add(new Source("mk", "Фокус","http://fokus.mk/"));
		sources.add(new Source("mk", "Курир","http://www.kurir.mk/"));
		sources.add(new Source("mk", "Либертас","http://www.libertas.mk/"));
		sources.add(new Source("mk", "Нова ТВ","http://novatv.mk/"));
		sources.add(new Source("mk", "Република","http://republika.mk/"));
		sources.add(new Source("mk", "Телма","http://www.telma.com.mk"));
		sources.add(new Source("mk", "Утрински весник","http://www.utrinski.mk/"));
		sources.add(new Source("mk", "Вечер","http://vecer.mk"));
		sources.add(new Source("mk", "Вест","http://www.vest.mk"));
		sources.add(new Source("mk", "24 вести","http://24vesti.mk"));
		sources.add(new Source("en", "GOOGLE_NEWS","http://news.google.com/"));
		sources.add(new Source("en", "HUFFINGTON_POST","http://www.huffingtonpost.com/"));
		return sources;
	}
	
	@RequestMapping(value={"/contents/{date}/{preferences}"}, method=RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Content> getContentsByDateAndDate(@PathVariable String date, @PathVariable List<String> preferences){
		return selector.getContentsByDateAndDate(date, preferences);
	}
	
}