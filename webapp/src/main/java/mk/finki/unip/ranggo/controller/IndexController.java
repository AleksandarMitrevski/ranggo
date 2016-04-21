package mk.finki.unip.ranggo.controller;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Date;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import mk.finki.unip.ranggo.model.Content;
import mk.finki.unip.ranggo.model.Person;
import mk.finki.unip.ranggo.service.ContentsAggregator;
import mk.finki.unip.ranggo.service.ContentsAggregatorException;
import mk.finki.unip.ranggo.service.ContentsSelector;

@Controller
public class IndexController {
	
	@Autowired
	private ContentsAggregator aggregator;
	
	@Autowired
	private ContentsSelector selector;
	
	@RequestMapping(value={"", "/"}, method=RequestMethod.GET)
	public ModelAndView index() {
		return new ModelAndView("index");
	}
	
	//each invocation currently costs 120 AlchemyAPI transactions 
	@RequestMapping(value={"/update"}, method=RequestMethod.GET)
	@ResponseBody
	public String update(@RequestParam(required=false) String date){
		//if 'date' is not specified, the method aggregates contents published on the current date
		//if it is, it aggregates the data published on that date
		//parameter format is dd.mm.yyyy, assumed UTC+0
		
		//parse or generate date object 
		Date dateObj = null;
		if(date != null){
			try{
				final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.mm.yyyy");
				simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
				dateObj = simpleDateFormat.parse(date);
				
			}catch(ParseException e){}
		}else{
			//no date parameter, default case is today
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
			dateObj = calendar.getTime();
		}
		
	    if(dateObj != null){
			boolean googlenews = true, facebook = true, twitter = true, other = true;
			
			try{
				aggregator.aggregateGoogleNewsRSSFeed(dateObj);
			}catch(ContentsAggregatorException exception){
				googlenews = false;
			}
			
			try{
				aggregator.aggregateFacebook(dateObj);
			}catch(ContentsAggregatorException exception){
				facebook = false;
			}
			
			try{
				aggregator.aggregateTwitter(dateObj);
			}catch(ContentsAggregatorException exception){
				twitter = false;
			}
			
			try{
				aggregator.aggregateOther(dateObj);
			}catch(ContentsAggregatorException exception){
				other = false;
			}
			
			//return operation status
			if(googlenews && facebook && twitter && other){
				return "ok";
			}else if(!googlenews && !facebook && !twitter && !other){
				return "failed";
			}else{
				return "partial";
			}
	    }else{
	    	return "invalid from/to parameters";
	    }
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