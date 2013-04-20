package com.ecmdeveloper.plugin.folderview.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;

public class Categories {

	private final Category root;
	
	public Categories(List<IObjectStoreItem> items, List<ColumnDescription> columnDescriptions) {
		
		root = new Category("root", "", 0, items);
		
		List<Category> categories = new ArrayList<Category>();
		List<Category> subCategories = new ArrayList<Category>();
		categories.add(root);
		int categoryLevel = 0;
		
		for ( ColumnDescription columnDescription : columnDescriptions ) {
			
			++categoryLevel;
			
			for ( Category category : categories ) {
				Collection<Category> splitInCategories = splitInCategories(columnDescription.getName(),
						categoryLevel, category.getChildren());
				category.setChildren(splitInCategories);
				subCategories.addAll(splitInCategories);
			}
			
			categories.clear();
			categories.addAll( subCategories );
			subCategories.clear();
		}
	}
	
	private List<Category> splitInCategories(String categoryName, int categoryLevel, Collection<?> items ) {
		
		List<Category> categories = new ArrayList<Category>();
		Map<Object, Category> valueToCategory = new HashMap<Object, Category>();
		
		for ( Object item : items ) {

			if ( item instanceof IObjectStoreItem ) 
			{
				Object value = ((IObjectStoreItem) item).getSafeValue( categoryName );
				if ( value instanceof Date) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime( (Date) value );
					calendar.set(Calendar.HOUR, 0);
					calendar.set(Calendar.MINUTE, 0);
					calendar.set(Calendar.SECOND, 0);
					calendar.set(Calendar.MILLISECOND, 0);
					value = calendar.getTime();
				}
				
				System.out.println( value );
				Category valueCategory = valueToCategory.get(value);;
				
				if ( valueCategory == null ) {
					valueCategory = new Category(categoryName, value, categoryLevel);
					valueToCategory.put( value, valueCategory );
					categories.add(valueCategory);
				} 
				
				valueCategory.add(item);
			}
		}

		return categories;
	}

	public Category getRoot() {
		return root;
	}
	
	public static void printTree(Collection<?> children, String prefix ) {
		
		for (Iterator<?> iterator = children.iterator(); iterator.hasNext();) 
		{
			Object item = iterator.next();
			if (item instanceof Category) {
				Category category = (Category) item;
        	
	    		System.out.println( prefix + "| " );
	    		System.out.print( prefix );
	    		System.out.print( iterator.hasNext() ?  "+-" : "\\-" );
        	
	    		System.out.println( category.getValue().toString() );

	    		printTree(category.getChildren(), prefix + ( iterator.hasNext() ? "| " : " " ) );
			} else {
	    		System.out.println( prefix + "| " );
	    		System.out.print( prefix );
	    		System.out.print( iterator.hasNext() ?  "+-" : "\\-" );
	    		System.out.println( item.toString() );
				
			}
		}
		
	}
}
