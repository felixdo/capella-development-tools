rules(cat, constraint) ::= <<
<?xml version='1.0' encoding='utf-8' ?><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<link rel="stylesheet" type="text/css" href="PLUGINS_ROOT/org.polarsys.capella.doc/html/styles.css" media="all" />
		<link rel="stylesheet" type="text/css" href="PLUGINS_ROOT/org.polarsys.capella.validation.doc/html/css/styles.css"/>
	</head>
	<body>
	    <h1>$cat.qualifiedName$</h1>
$constraint:constr(); separator="\n"$
	</body>
</html>
>>