package org.nmdp.HLAGene;

import java.util.Arrays;
import java.util.List;

public enum SectionName {
	US(0, 0, 1),
	e1(1, 1, 1),
	i1(0, 2, 1),
	e2(1, 3, 2),
	i2(0, 4, 2),
	e3(1, 5, 3),
	i3(0, 6, 3),
	e4(1, 7, 4),
	i4(0, 8, 4),
	e5(1, 9, 5),
	i5(0, 10, 5),
	e6(1, 11, 6),
	i6(0, 12, 6),
	e7(1, 13, 7),
	i7(0, 14, 7),
	e8(1, 15, 8),
	DS(0, 16, 1);

	private int value;
	private int order;
	private int minOrder;

	SectionName(int value, int order, int mo){
		this.value = value;
		this.order = order;
		minOrder = mo;
	}

	public SectionName get(int order){
		List<SectionName> values = Arrays.asList(SectionName.values());
		for(SectionName  item : values){
			if(item.order == order){
				return item;
			}
		}
		return null;
	}

	public boolean isExon(){
		return this.value == 1;
	}

	public int getOrder(SectionName sn){
		return sn.order;
	}

	public int getNumber(){
		return this.minOrder;
	}
}
