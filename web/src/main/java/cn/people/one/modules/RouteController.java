package cn.people.one.modules;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 功能模块页面的路由
 */
@Controller
public class RouteController {

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index()
    {
        return "redirect:/cms.html";
    }

	@RequestMapping(value = "dashboard.html", method = RequestMethod.GET)
	public String dashboard() {
		return "dashboard";
	}

	@RequestMapping(value = "sys.html", method = RequestMethod.GET)
	public String sysIndex() {
		return "sys";
	}

	@RequestMapping(value = "users.html", method = RequestMethod.GET)
	public String usersIndex() {
		return "users";
	}

	@RequestMapping(value = "cms.html", method = RequestMethod.GET)
	public String cmsIndex() {
		return "cms";
	}

	@RequestMapping(value = "analysis.html", method = RequestMethod.GET)
	public String analysisIndex() {
		return "analysis";
	}

	@RequestMapping(value = "client.html", method = RequestMethod.GET)
	public String appIndex() {
		return "client";
	}

	@RequestMapping(value = "example.html", method = RequestMethod.GET)
	public String exampleIndex() {
		return "example";
	}

    @RequestMapping(value = "myCenter.html", method = RequestMethod.GET)
    public String myCenter() {
        return "myCenter";
    }

    @RequestMapping(value = "news.html", method = RequestMethod.GET)
    public String news() {
        return "news";
    }

    @RequestMapping(value = "ask.html", method = RequestMethod.GET)
    public String ask() {
        return "ask";
    }
}
