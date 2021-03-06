package com.marvinformatics.kiss.matchers.path.matcher;

import java.nio.file.Path;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * <p>WithNameMatcher class.</p>
 *
 * @author Marvin
 * @since 0.7
 */
public class WithNameMatcher
    extends AbstractPathMatcher
{
    private final Matcher<String> name;

    private String realName;

    /**
     * <p>Constructor for WithNameMatcher.</p>
     */
    public WithNameMatcher( Matcher<String> name )
    {
        this.name = name;
    }

    /** {@inheritDoc} */
    @Override
    public boolean matchesSafely( Path item )
    {
        realName = item.toString();
        return name.matches( realName );
    }

    /** {@inheritDoc} */
    public void describeTo( Description description )
    {
        description.appendText( "with name path '" );
        description.appendDescriptionOf( name );
        description.appendText( "'" );
    }

    @Override
    protected void describeMismatchSafely( Path item, Description description )
    {
        description.appendText( " that path " );
        description.appendValue( item );
        description.appendText( " with name " );
        description.appendDescriptionOf( name );
        description.appendText( ", not " + realName );
    }

}
