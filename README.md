
## Introduction

This is a GWT project for creating amazing presentations with GWT.

## Examples
GQuery [presentations](http://manolo.github.io/gwt-slides/) at GWT.create Conferences

## The Basics

It uses gwt + gquery + generators to generate a HTML5 presentation based on the example code and javadoc in a java file.

All 'slide' prefixed methods in your presentation java class will be merged in the main html
page. Javadocs of the methods will be written as tittle, bubtittle
and sections in the slides. The body of the the function will be
included in the code section of the slide.

There are some conventions in the body, like preceding a
line with `@ ` meaning wrap it with a `<h1>`, `@@ ` with `<h4>`,
or `- ` and `-- ` for creating lists. Also is allowed that you write
html code in javadoc blocks.

You can include any method or inner class in the slide just
writing `@include: methodName` or `@include: className` anywhere (javadoc, code).


The body code will be executed when clicking on the '#play' button,
but additionally, you can write extra functions which will be executed
when entering the slide, leaving it, before running the code or after.

So the convention for method names is:

`enterMethod, beforeMethod, slideMethod, afterMethod, leaveMethod.`

If you define your slide in your hosted html, its content will be automatically
merged to the code coming from the method in the java class. 
Each section should have an unique `id` matching the
name of the `slideMethodName`, but in lowercase: `methodname`. The order of
these sections will be the order in the java file.

`<section id='mehodname'>Extra Content</section>`


## Usage

If you want to do your own presentation, copy all the content in the
`org/gquery/slides`, `org/gquery/public` folders and the `org/gquery/Slides.gwt.xml` file.

Then create your own `EntryPoint` like:

```
  public class GwtCreate2013EntryPoint implements EntryPoint {
    public void onModuleLoad() {
       new Slides(GWT.create(MyGwtPresentation.class));
    }
  }
```

Finally create your presentation class:
```
  public class MyGwtPresentation extends SlidesSource {
    /**
     * @ My GWT presentation
     * @@ By My Name
     */
    public void slideIntro() {
    }
  }

```
