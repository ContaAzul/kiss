package com.marvinformatics.kiss.matchers.file.matcher;

import java.io.File;

import org.hamcrest.Description;

public class ExistsMatcher
    extends AbstractFileMatcher
{

    @Override
    public boolean matchesSafely( File item )
    {
        return item.exists();
    }

    public void describeTo( Description description )
    {
        description.appendText( "to exists" );
    }

    @Override
    protected void describeMismatchSafely( File item, Description description )
    {
        description.appendValue( item );
        description.appendText( " not found!" );
    }
}