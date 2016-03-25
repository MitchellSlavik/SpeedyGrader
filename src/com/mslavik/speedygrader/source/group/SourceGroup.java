package com.mslavik.speedygrader.source.group;

import java.io.File;

import com.mslavik.speedygrader.source.SourceFile;
import com.mslavik.speedygrader.source.SourceType;

public abstract class SourceGroup extends SourceFile{

	protected SourceGroup(SourceType type, File originalFileLoc) {
		super(type, originalFileLoc);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String compile() {
		// TODO Auto-generated method stub
		return null;
	}

}
