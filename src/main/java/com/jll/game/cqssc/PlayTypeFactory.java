package com.jll.game.cqssc;

import java.util.ArrayList;
import java.util.List;

import com.jll.game.playtype.PlayTypeFacade;

public class PlayTypeFactory {

	private static PlayTypeFactory factory;
	
	
	private List<PlayTypeFacade> playTypeFacades = new ArrayList<>();
	
	private PlayTypeFactory() {
		QszxPlayTypeFacadeImpl qszxPlayTypeFacadeImpl = new QszxPlayTypeFacadeImpl();
		/*BdwHsPlayTypeFacadeImpl bdwHsPlayTypeFacadeImpl = new BdwHsPlayTypeFacadeImpl();
		BdwQsPlayTypeFacadeImpl bdwQsPlayTypeFacadeImpl = new BdwQsPlayTypeFacadeImpl();
		BdwZsPlayTypeFacadeImpl bdwZsPlayTypeFacadeImpl = new BdwZsPlayTypeFacadeImpl();
		DwdPlayTypeFacadeImpl dwdPlayTypeFacadeImpl = new DwdPlayTypeFacadeImpl();
		HszuxHhzxPlayTypeFacadeImpl hszuxHhzxPlayTypeFacadeImpl = new HszuxHhzxPlayTypeFacadeImpl();
		HszuxZlPlayTypeFacadeImpl hszuxZlPlayTypeFacadeImpl = new HszuxZlPlayTypeFacadeImpl();
		HszuxZsPlayTypeFacadeImpl hszuxZsPlayTypeFacadeImpl = new HszuxZsPlayTypeFacadeImpl();
		HszxPlayTypeFacadeImpl hszxPlayTypeFacadeImpl = new HszxPlayTypeFacadeImpl();
		QszuxZlPlayTypeFacadeImpl qszuxZlPlayTypeFacadeImpl = new QszuxZlPlayTypeFacadeImpl();
		QszuxZsPlayTypeFacadeImpl qszuxZsPlayTypeFacadeImpl = new QszuxZsPlayTypeFacadeImpl();
		Wxq2PlayTypeFacadeImpl wxq2PlayTypeFacadeImpl = new Wxq2PlayTypeFacadeImpl();
		Wxq2ZxPlayTypeFacadeImpl wxq2ZxPlayTypeFacadeImpl = new Wxq2ZxPlayTypeFacadeImpl();
		ZszuxHhzxPlayTypeFacadeImpl zszuxHhzxPlayTypeFacadeImpl = new ZszuxHhzxPlayTypeFacadeImpl();
		ZszuxZlPlayTypeFacadeImpl zszuxZlPlayTypeFacadeImpl = new ZszuxZlPlayTypeFacadeImpl();
		ZszuxZsPlayTypeFacadeImpl zszuxZsPlayTypeFacadeImpl = new ZszuxZsPlayTypeFacadeImpl();
		ZszxPlayTypeFacadeImpl zszxPlayTypeFacadeImpl = new ZszxPlayTypeFacadeImpl();
		
		playTypeFacades.add(bdwHsPlayTypeFacadeImpl);
		playTypeFacades.add(bdwQsPlayTypeFacadeImpl);
		playTypeFacades.add(bdwZsPlayTypeFacadeImpl);
		playTypeFacades.add(dwdPlayTypeFacadeImpl);
		playTypeFacades.add(hszuxHhzxPlayTypeFacadeImpl);
		playTypeFacades.add(hszuxZlPlayTypeFacadeImpl);
		playTypeFacades.add(hszuxZsPlayTypeFacadeImpl);
		playTypeFacades.add(hszxPlayTypeFacadeImpl);
		playTypeFacades.add(qszuxZlPlayTypeFacadeImpl);
		playTypeFacades.add(qszuxZsPlayTypeFacadeImpl);
		playTypeFacades.add(wxq2PlayTypeFacadeImpl);
		playTypeFacades.add(wxq2ZxPlayTypeFacadeImpl);
		playTypeFacades.add(zszuxHhzxPlayTypeFacadeImpl);
		playTypeFacades.add(zszuxZlPlayTypeFacadeImpl);
		playTypeFacades.add(zszuxZsPlayTypeFacadeImpl);
		playTypeFacades.add(zszxPlayTypeFacadeImpl);*/
		
		playTypeFacades.add(qszxPlayTypeFacadeImpl);
	}
	
	public static PlayTypeFactory getInstance() {
		if(factory == null) {
			factory = new PlayTypeFactory();
		}
		
		return factory;
	}
	
	public PlayTypeFacade getPlayTypeFacade(String facadeName) {
		for(PlayTypeFacade playTypeFacade : playTypeFacades) {
			if(facadeName.contains(playTypeFacade.getPlayTypeDesc())
					|| playTypeFacade.getPlayTypeDesc().equals(facadeName)) {
				return playTypeFacade;
			}
		}
		
		return null;
	}
}
