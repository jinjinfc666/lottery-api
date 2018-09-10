package com.jll.game.playtypefacade;

import java.util.ArrayList;
import java.util.List;

import com.jll.game.playtype.PlayTypeFacade;

public class PlayTypeFactory {

	private static PlayTypeFactory factory;
	
	
	private List<PlayTypeFacade> playTypeFacades = new ArrayList<>();
	
	private PlayTypeFactory() {
		QszxPlayTypeFacadeImpl qszxPlayTypeFacadeImpl = new QszxPlayTypeFacadeImpl();
		ZszxPlayTypeFacadeImpl zszxPlayTypeFacadeImpl = new ZszxPlayTypeFacadeImpl();
		HszxPlayTypeFacadeImpl hszxPlayTypeFacadeImpl = new HszxPlayTypeFacadeImpl();
		QszuxZsPlayTypeFacadeImpl qszuxZsPlayTypeFacadeImpl = new QszuxZsPlayTypeFacadeImpl();
		QszuxZlPlayTypeFacadeImpl qszuxZlPlayTypeFacadeImpl = new QszuxZlPlayTypeFacadeImpl();
		QszuxMixPlayTypeFacadeImpl qszuxMixPlayTypeFacadeImpl = new QszuxMixPlayTypeFacadeImpl();
		ZszuxZsPlayTypeFacadeImpl zszuxZsPlayTypeFacadeImpl = new ZszuxZsPlayTypeFacadeImpl();
		ZszuxZlPlayTypeFacadeImpl zszuxZlPlayTypeFacadeImpl = new ZszuxZlPlayTypeFacadeImpl();
		ZszuxHhzxPlayTypeFacadeImpl zszuxHhzxPlayTypeFacadeImpl = new ZszuxHhzxPlayTypeFacadeImpl();
		HszuxZsPlayTypeFacadeImpl hszuxZsPlayTypeFacadeImpl = new HszuxZsPlayTypeFacadeImpl();
		HszuxZlPlayTypeFacadeImpl hszuxZlPlayTypeFacadeImpl = new HszuxZlPlayTypeFacadeImpl();
		HszuxHhzxPlayTypeFacadeImpl hszuxHhzxPlayTypeFacadeImpl = new HszuxHhzxPlayTypeFacadeImpl();
		Wxq2PlayTypeFacadeImpl wxq2PlayTypeFacadeImpl = new Wxq2PlayTypeFacadeImpl();
		Wxq2ZxPlayTypeFacadeImpl wxq2ZxPlayTypeFacadeImpl = new Wxq2ZxPlayTypeFacadeImpl();
		Wxh2PlayTypeFacadeImpl wxh2PlayTypeFacadeImpl = new Wxh2PlayTypeFacadeImpl();
		Wxh2ZxPlayTypeFacadeImpl wxh2ZxPlayTypeFacadeImpl = new Wxh2ZxPlayTypeFacadeImpl();
		BdwQsPlayTypeFacadeImpl bdwQsPlayTypeFacadeImpl = new BdwQsPlayTypeFacadeImpl();
		BdwZsPlayTypeFacadeImpl bdwZsPlayTypeFacadeImpl = new BdwZsPlayTypeFacadeImpl();
		BdwHsPlayTypeFacadeImpl bdwHsPlayTypeFacadeImpl = new BdwHsPlayTypeFacadeImpl();
		DwdPlayTypeFacadeImpl dwdPlayTypeFacadeImpl = new DwdPlayTypeFacadeImpl();
		EleIn5QszxPlayTypeFacadeImpl eleIn5PlayTypeFacadeImpl = new EleIn5QszxPlayTypeFacadeImpl();
		EleIn5QszuxZlPlayTypeFacadeImpl eleIn5QszuxPlayTypeFacadeImpl = new EleIn5QszuxZlPlayTypeFacadeImpl();
		EleIn5Wxq2PlayTypeFacadeImpl eleIn5Wxq2PlayTypeFacadeImpl = new EleIn5Wxq2PlayTypeFacadeImpl();
		EleIn5Wxh2ZxPlayTypeFacadeImpl eleIn5Wxh2ZxPlayTypeFacadeImpl = new EleIn5Wxh2ZxPlayTypeFacadeImpl();
		EleIn5BdwQsPlayTypeFacadeImpl eleIn5BdwQsPlayTypeFacadeImpl = new EleIn5BdwQsPlayTypeFacadeImpl();
		EleIn5DwdPlayTypeFacadeImpl eleIn5DwdPlayTypeFacadeImpl = new EleIn5DwdPlayTypeFacadeImpl();
		EleIn5QwDsPlayTypeFacadeImpl eleIn5QwDsPlayTypeFacadeImpl = new EleIn5QwDsPlayTypeFacadeImpl();
		EleIn5QwZwPlayTypeFacadeImpl eleIn5QwZwPlayTypeFacadeImpl = new EleIn5QwZwPlayTypeFacadeImpl();
		EleIn5Rx1PlayTypeFacadeImpl eleIn5Rx1PlayTypeFacadeImpl = new EleIn5Rx1PlayTypeFacadeImpl();
		EleIn5Rx2PlayTypeFacadeImpl eleIn5Rx2PlayTypeFacadeImpl = new EleIn5Rx2PlayTypeFacadeImpl();
		EleIn5Rx3PlayTypeFacadeImpl eleIn5Rx3PlayTypeFacadeImpl = new EleIn5Rx3PlayTypeFacadeImpl();
		EleIn5Rx4PlayTypeFacadeImpl eleIn5Rx4PlayTypeFacadeImpl = new EleIn5Rx4PlayTypeFacadeImpl();
		EleIn5Rx5PlayTypeFacadeImpl eleIn5Rx5PlayTypeFacadeImpl = new EleIn5Rx5PlayTypeFacadeImpl();
		EleIn5Rx6PlayTypeFacadeImpl eleIn5Rx6PlayTypeFacadeImpl = new EleIn5Rx6PlayTypeFacadeImpl();
		EleIn5Rx7PlayTypeFacadeImpl eleIn5Rx7PlayTypeFacadeImpl = new EleIn5Rx7PlayTypeFacadeImpl();
		EleIn5Rx8PlayTypeFacadeImpl eleIn5Rx8PlayTypeFacadeImpl = new EleIn5Rx8PlayTypeFacadeImpl();
		Pk10QyzxPlayTypeFacadeImpl pk10QyzxPlayTypeFacadeImpl = new Pk10QyzxPlayTypeFacadeImpl();
		Pk10QezxPlayTypeFacadeImpl pk10QezxPlayTypeFacadeImpl = new Pk10QezxPlayTypeFacadeImpl();
		Pk10QszxPlayTypeFacadeImpl pk10QszxPlayTypeFacadeImpl = new Pk10QszxPlayTypeFacadeImpl();
		Pk10DwdPlayTypeFacadeImpl pk10DwdPlayTypeFacadeImpl = new Pk10DwdPlayTypeFacadeImpl();
		Pk10Lh1V10PlayTypeFacadeImpl pk10Lh1V10PlayTypeFacadeImpl = new Pk10Lh1V10PlayTypeFacadeImpl();
		Pk10Lh2V9PlayTypeFacadeImpl pk10Lh2V9PlayTypeFacadeImpl = new Pk10Lh2V9PlayTypeFacadeImpl();
		Pk10Lh3V8PlayTypeFacadeImpl pk10Lh3V8PlayTypeFacadeImpl = new Pk10Lh3V8PlayTypeFacadeImpl();
		Pk10Lh4V7PlayTypeFacadeImpl pk10Lh4V7PlayTypeFacadeImpl = new Pk10Lh4V7PlayTypeFacadeImpl();
		Pk10Lh5V6PlayTypeFacadeImpl pk10Lh5V6PlayTypeFacadeImpl = new Pk10Lh5V6PlayTypeFacadeImpl();
		Pk10Ds1PlayTypeFacadeImpl pk10Ds1PlayTypeFacadeImpl = new Pk10Ds1PlayTypeFacadeImpl();
		Pk10Ds2PlayTypeFacadeImpl pk10Ds2PlayTypeFacadeImpl = new Pk10Ds2PlayTypeFacadeImpl();
		Pk10Ds3PlayTypeFacadeImpl pk10Ds3PlayTypeFacadeImpl = new Pk10Ds3PlayTypeFacadeImpl();
		
		playTypeFacades.add(qszxPlayTypeFacadeImpl);
		playTypeFacades.add(zszxPlayTypeFacadeImpl);
		playTypeFacades.add(hszxPlayTypeFacadeImpl);
		playTypeFacades.add(qszuxZsPlayTypeFacadeImpl);
		playTypeFacades.add(qszuxZlPlayTypeFacadeImpl);
		playTypeFacades.add(qszuxMixPlayTypeFacadeImpl);
		playTypeFacades.add(zszuxZsPlayTypeFacadeImpl);
		playTypeFacades.add(zszuxZlPlayTypeFacadeImpl);
		playTypeFacades.add(zszuxHhzxPlayTypeFacadeImpl);
		playTypeFacades.add(hszuxZsPlayTypeFacadeImpl);
		playTypeFacades.add(hszuxZlPlayTypeFacadeImpl);
		playTypeFacades.add(hszuxHhzxPlayTypeFacadeImpl);
		playTypeFacades.add(wxq2PlayTypeFacadeImpl);
		playTypeFacades.add(wxq2ZxPlayTypeFacadeImpl);
		playTypeFacades.add(wxh2PlayTypeFacadeImpl);
		playTypeFacades.add(wxh2ZxPlayTypeFacadeImpl);
		playTypeFacades.add(bdwQsPlayTypeFacadeImpl);
		playTypeFacades.add(bdwZsPlayTypeFacadeImpl);
		playTypeFacades.add(bdwHsPlayTypeFacadeImpl);
		playTypeFacades.add(dwdPlayTypeFacadeImpl);
		playTypeFacades.add(eleIn5PlayTypeFacadeImpl);
		playTypeFacades.add(eleIn5QszuxPlayTypeFacadeImpl);
		playTypeFacades.add(eleIn5Wxq2PlayTypeFacadeImpl);
		playTypeFacades.add(eleIn5Wxh2ZxPlayTypeFacadeImpl);
		playTypeFacades.add(eleIn5BdwQsPlayTypeFacadeImpl);
		playTypeFacades.add(eleIn5DwdPlayTypeFacadeImpl);
		playTypeFacades.add(eleIn5QwDsPlayTypeFacadeImpl);
		playTypeFacades.add(eleIn5QwZwPlayTypeFacadeImpl);
		playTypeFacades.add(eleIn5Rx1PlayTypeFacadeImpl);
		playTypeFacades.add(eleIn5Rx2PlayTypeFacadeImpl);
		playTypeFacades.add(eleIn5Rx3PlayTypeFacadeImpl);
		playTypeFacades.add(eleIn5Rx4PlayTypeFacadeImpl);
		playTypeFacades.add(eleIn5Rx5PlayTypeFacadeImpl);
		playTypeFacades.add(eleIn5Rx6PlayTypeFacadeImpl);
		playTypeFacades.add(eleIn5Rx7PlayTypeFacadeImpl);
		playTypeFacades.add(eleIn5Rx8PlayTypeFacadeImpl);
		playTypeFacades.add(pk10QyzxPlayTypeFacadeImpl);
		playTypeFacades.add(pk10QezxPlayTypeFacadeImpl);
		playTypeFacades.add(pk10QszxPlayTypeFacadeImpl);
		playTypeFacades.add(pk10DwdPlayTypeFacadeImpl);
		playTypeFacades.add(pk10Lh1V10PlayTypeFacadeImpl);
		playTypeFacades.add(pk10Lh2V9PlayTypeFacadeImpl);
		playTypeFacades.add(pk10Lh3V8PlayTypeFacadeImpl);
		playTypeFacades.add(pk10Lh4V7PlayTypeFacadeImpl);
		playTypeFacades.add(pk10Lh5V6PlayTypeFacadeImpl);
		playTypeFacades.add(pk10Ds1PlayTypeFacadeImpl);
		playTypeFacades.add(pk10Ds2PlayTypeFacadeImpl);
		playTypeFacades.add(pk10Ds3PlayTypeFacadeImpl);
	}
	
	public static PlayTypeFactory getInstance() {
		if(factory == null) {
			factory = new PlayTypeFactory();
		}
		
		return factory;
	}
	
	public PlayTypeFacade getPlayTypeFacade(String facadeName) {
		for(PlayTypeFacade playTypeFacade : playTypeFacades) {
			if(playTypeFacade.getPlayTypeDesc().equals(facadeName)) {
				return playTypeFacade;
			}
		}
		
		return null;
	}
}
