

This project is a presentation of gwtquery Promises and Deferred API.

It uses gwt + gquery + generators to generate a HTML5 presentation based on the example code and javadoc in a java file.

All tests methods in your presentation java class will be merged in the main html
page. Javadocs of the methods will be written as tittle, bubtittle
and sections in the slides. The body of the the function will be
included in the code section of the slide.

There are some conventions in the body, like preceding a
line with `@ ` meaning wrap it with a `<h1>`, `@@ ` with `<h4>`,
or `- ` with `<ul>` sections.

You can include any method or inner class in the slide just
writing `@include: methodName` or `@include: className`anywhere (javadoc, code).
Also an extra html is allowed in javadocs.

The body code will be executed when clicking on the '#play' button,
but aditionally, you can write extra functions which will be executed
when entering the slide, leaving it, before running the code or after.

So the convention for method names are:

`enterMethod, beforeMethod, testMethod, afterMetod, leaveMethod.`

In your hosted html page you have to define each slide, and any
extra content in the slide apart from the content automatically merged
from this class. Each section should have an unique `id` matching the
name of the `testSlideMethodName`, but in lowercase: `slidemethodname`. The order of
these sections will be the order of the slides despite the order of
test methods in this class.

`<section id='slidemehodname'>Extra Content</section>`
