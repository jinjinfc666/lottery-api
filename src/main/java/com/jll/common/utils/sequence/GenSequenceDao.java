package com.jll.common.utils.sequence;

import java.util.List;

import com.jll.entity.GenSequence;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;

public interface GenSequenceDao
{
	GenSequence querySeqVal();

	GenSequence queryPK10SeqVal();
	
	void saveSeq(GenSequence seq);
		
	
}
