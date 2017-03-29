/*******************************************************************************
 * Copyright (c) 2017 Felix Dorner
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *    Felix Dorner
 *******************************************************************************/
package org.polarsys.capella.tools.validationdocgen;

import java.io.File;
import java.util.Comparator;

import org.eclipse.emf.validation.model.Category;
import org.eclipse.emf.validation.service.ConstraintRegistry;
import org.eclipse.emf.validation.service.IConstraintDescriptor;
import org.eclipse.emf.validation.service.ModelValidationService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupDir;
import org.stringtemplate.v4.misc.ErrorBuffer;
import org.stringtemplate.v4.misc.STMessage;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;

/**
 * Generate documentation for capella validation rules
 * 
 * @author Felix Dorner
 */
public class GeneratorApp implements IApplication {

  private final String rootCategoryId = "capella.category"; //$NON-NLS-1$
  private final String targetHtmlFileName = "ValidationRules.html"; //$NON-NLS-1$

  @Override
  public Object start(IApplicationContext context) throws Exception {

    String[] args = (String[]) context.getArguments().get(IApplicationContext.APPLICATION_ARGS);

    ModelValidationService service = ModelValidationService.getInstance();
    service.loadXmlConstraintDeclarations();

    ConstraintRegistry reg = ConstraintRegistry.getInstance();

    Comparator<IConstraintDescriptor> comparator = new Comparator<IConstraintDescriptor>() {
      @Override
      public int compare(IConstraintDescriptor o1, IConstraintDescriptor o2) {
        return ComparisonChain.start().
        // uncomment the next line to order by severity before ordering by name
        // compare(o1.getSeverity().getValue(), o2.getSeverity().getValue()).
        compare(o1.getName(), o2.getName()).result();
      }
    };

    Multimap<Category, IConstraintDescriptor> map = TreeMultimap.create(Ordering.arbitrary(), comparator);
    int colwidth = 0;
    for (IConstraintDescriptor d : reg.getAllDescriptors()) {
      Category cat = d.getCategories().iterator().next();
      if (cat.getPath().startsWith(rootCategoryId)) {
        map.put(cat, d);
        colwidth = Math.max(cat.getPath().length(), colwidth);
      }

    }

    ErrorBuffer b = new ErrorBuffer();
    System.out.println();
    System.out.println("============================== Generating your documentation =========================="); //$NON-NLS-1$
    System.out.println();
    System.out.println("Basedir is " + args[1]); //$NON-NLS-1$
    System.out.println();
    for (Category c : map.keySet()) {

      String relpath = c.getPath().substring(rootCategoryId.length()) + System.getProperty("file.separator") //$NON-NLS-1$
          + targetHtmlFileName;

      File targetFile = new File(new File(args[1]), relpath);
      System.out.println("Writing " + relpath); //$NON-NLS-1$

      STGroup group = new STGroupDir(args[0], "UTF-8", '$', '$'); //$NON-NLS-1$

      ST rules = group.getInstanceOf("rules"); //$NON-NLS-1$
      rules.add("cat", c); //$NON-NLS-1$
      rules.add("constraint", map.get(c)); //$NON-NLS-1$
      rules.write(targetFile, b);
    }
    for (STMessage m : b.errors) {
      System.err.println(m);
    }
    return Math.min(b.errors.size(), 1);
  }

  @Override
  public void stop() {
  }

}
