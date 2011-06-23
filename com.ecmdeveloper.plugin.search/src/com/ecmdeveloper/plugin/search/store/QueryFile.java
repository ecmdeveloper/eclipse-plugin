/**
 * Copyright 2011, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in
 * is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */

package com.ecmdeveloper.plugin.search.store;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

import com.ecmdeveloper.plugin.search.model.AndContainer;
import com.ecmdeveloper.plugin.search.model.ClassTest;
import com.ecmdeveloper.plugin.search.model.Comparison;
import com.ecmdeveloper.plugin.search.model.ComparisonOperation;
import com.ecmdeveloper.plugin.search.model.FreeText;
import com.ecmdeveloper.plugin.search.model.FullTextQuery;
import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.IQueryTable;
import com.ecmdeveloper.plugin.search.model.InFolderTest;
import com.ecmdeveloper.plugin.search.model.InSubFolderTest;
import com.ecmdeveloper.plugin.search.model.NotContainer;
import com.ecmdeveloper.plugin.search.model.NullTest;
import com.ecmdeveloper.plugin.search.model.OrContainer;
import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.model.QueryComponent;
import com.ecmdeveloper.plugin.search.model.QueryContainer;
import com.ecmdeveloper.plugin.search.model.QueryDiagram;
import com.ecmdeveloper.plugin.search.model.QueryElement;
import com.ecmdeveloper.plugin.search.model.QueryField2;
import com.ecmdeveloper.plugin.search.model.QueryFieldType;
import com.ecmdeveloper.plugin.search.model.QuerySubpart;
import com.ecmdeveloper.plugin.search.model.QueryTable2;
import com.ecmdeveloper.plugin.search.model.SortType;
import com.ecmdeveloper.plugin.search.model.ThisQueryField;
import com.ecmdeveloper.plugin.search.model.WildcardTest;
import com.ecmdeveloper.plugin.search.model.constants.FullTextQueryType;
import com.ecmdeveloper.plugin.search.model.constants.QueryComponentType;
import com.ecmdeveloper.plugin.search.model.constants.QueryContainerType;
import com.ecmdeveloper.plugin.search.model.constants.WildcardType;
import com.ecmdeveloper.plugin.search.util.PluginTagNames;

/**
 * @author ricardo.belfor
 *
 */
public class QueryFile {

	private static final String DATE_FORMAT = "yyyyMMdd HH:mm:ss";

	public static final int CURRENT_FILE_VERSION = 1;

	private File file;
	
	public QueryFile( File file ) {
		this.file = file;
	}
	
	public void save( Query query ) throws IOException {

		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			XMLMemento memento = getXMLMemento(query);
			memento.save(writer);
		} finally {
			try {
				if (writer != null)	writer.close();
			} catch (IOException e) {
				// PluginLog.error(e);
			}
		}
	}

	public XMLMemento getXMLMemento(Query query ) {
		
		XMLMemento memento = XMLMemento.createWriteRoot(PluginTagNames.QUERY);
		memento.putInteger(PluginTagNames.VERSION_TAG, CURRENT_FILE_VERSION );
		if ( query.getMaxCount() != null ) {
			memento.putInteger( PluginTagNames.MAX_COUNT, query.getMaxCount() );
		}
		memento.putBoolean( PluginTagNames.DISTINCT, query.isDistinct() );
		memento.putBoolean( PluginTagNames.INCLUDE_SUBCLASSES, query.isIncludeSubclasses() );
		memento.putString( PluginTagNames.NAME, query.getName() );
		memento.putString( PluginTagNames.SQL, query.toSQL() );
		
		initializeTablesChild(query.getQueryTables(), memento);
		initializeDiagramChild(query, memento);
		
		return memento;
	}

	private void initializeDiagramChild(Query query, XMLMemento memento) {
		IMemento diagramChild = memento.createChild(PluginTagNames.DIAGRAM); 
		QueryDiagram queryDiagram = query.getQueryDiagram();
		
		initializeQueryDiagramChildren(diagramChild, queryDiagram.getChildren() );
	}

	private void initializeQueryDiagramChildren(IMemento diagramChild, List<QueryElement> children) {
		IMemento queryComponentsChild = diagramChild.createChild(PluginTagNames.CHILDREN); 

		for (QueryElement child : children ) {
			if (child instanceof QueryComponent ) {
				IMemento queryComponentChild = queryComponentsChild.createChild(PluginTagNames.QUERY_COMPONENT);
				initializeQueryComponentChild(queryComponentChild, (QueryComponent) child );
			} else if ( child instanceof QueryContainer ) {
				IMemento queryContainerChild = queryComponentsChild.createChild(PluginTagNames.QUERY_CONTAINER);
				initializeQueryContainerChild(queryContainerChild, (QueryContainer) child );
			} else {
				throw new IllegalArgumentException();
			}
		}
	}

	private void initializeQueryContainerChild(IMemento queryContainerChild, QueryContainer queryContainer) {
		addLocation(queryContainer, queryContainerChild);
		addSize(queryContainer, queryContainerChild);
		queryContainerChild.putString(PluginTagNames.TYPE,  queryContainer.getType().name() );
		queryContainerChild.putBoolean(PluginTagNames.MAIN_QUERY,  queryContainer.isMainQuery() );
		initializeQueryDiagramChildren(queryContainerChild, queryContainer.getChildren() );
	}

	private void initializeQueryComponentChild(IMemento queryComponentChild, QueryComponent queryComponent ) {
		addLocation(queryComponent, queryComponentChild);
		addSize(queryComponent, queryComponentChild);
		queryComponentChild.putString(PluginTagNames.TYPE,  queryComponent.getType().name() );
		queryComponentChild.putBoolean(PluginTagNames.MAIN_QUERY,  queryComponent.isMainQuery() );
		
		IQueryField field = queryComponent.getField();
		if ( field != null ) {
			queryComponentChild.putString(PluginTagNames.FIELD_NAME,  field.getName() );
			queryComponentChild.putString(PluginTagNames.TABLE_NAME,  field.getQueryTable().getName() );
		}
		
		switch (queryComponent.getType()) {

		case COMPARISON:
			Comparison comparison = (Comparison) queryComponent;
			queryComponentChild.putString(PluginTagNames.COMPARISON_OPERATION,  comparison.getComparisonOperation().name() );
			addValue(queryComponentChild, comparison.getValue() );
			break;
			
		case FREE_TEXT:
			FreeText freeText = (FreeText) queryComponent;
			queryComponentChild.putString(PluginTagNames.TEXT,  freeText.getText() );
			break;

		case IN_FOLDER_TEST:
			InFolderTest inFolderTest = (InFolderTest) queryComponent;
			queryComponentChild.putString(PluginTagNames.FOLDER,  inFolderTest.getFolder() );
			break;
		
		case IN_SUBFOLDER_TEST:
			InSubFolderTest inSubFolderTest = (InSubFolderTest) queryComponent;
			queryComponentChild.putString(PluginTagNames.FOLDER,  inSubFolderTest.getFolder() );
			break;
		
		case NULL_TEST:
			NullTest nullTest = (NullTest) queryComponent;
			queryComponentChild.putBoolean(PluginTagNames.NEGATED, nullTest.isNegated() );
			break;
			
		case WILDCARD_TEST:
			WildcardTest wildcardTest = (WildcardTest) queryComponent;
			queryComponentChild.putString(PluginTagNames.WILDCARD_TYPE,  wildcardTest.getWildcardType().name() );
			queryComponentChild.putString(PluginTagNames.STRING_VALUE,  wildcardTest.getValue() );
			break;
		
		case CLASS_TEST:
			ClassTest classTest = (ClassTest) queryComponent;
			queryComponentChild.putString(PluginTagNames.CLASS_NAME,  classTest.getClassName() );
			break;
			
		case FULL_TEXT:
			FullTextQuery fullTextQuery = (FullTextQuery) queryComponent;
			queryComponentChild.putBoolean( PluginTagNames.ALL_FIELDS, fullTextQuery.isAllFields() );
			queryComponentChild.putString(PluginTagNames.TEXT, fullTextQuery.getText() );
			queryComponentChild.putString(PluginTagNames.FULL_TEXT_QUERY_TYPE, fullTextQuery.getFullTextQueryType().name() );
			break;
			
		default:
			throw new IllegalArgumentException();
		}
	}

	private void addValue(IMemento queryComponentChild, Object value) {
		
		if ( value != null ) {
			if (value instanceof Boolean ) {
				queryComponentChild.putBoolean( PluginTagNames.BOOLEAN_VALUE, (Boolean) value );
			} else if ( value instanceof String ) {
				queryComponentChild.putString(PluginTagNames.STRING_VALUE,  value.toString() );
			} else if ( value instanceof Date ) {
				SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);
				String dateString = dateFormatter.format(value);
				queryComponentChild.putString(PluginTagNames.DATE_VALUE, dateString );
			} else if ( value instanceof Integer ) {
				queryComponentChild.putString(PluginTagNames.INTEGER_VALUE,  value.toString() );
			} else if ( value instanceof Double ) {
				queryComponentChild.putString(PluginTagNames.DOUBLE_VALUE,  value.toString() );
			} else {
				throw new IllegalArgumentException();
			}
		}
	}

	private void addSize(QuerySubpart querySubpart, IMemento elementChild) {
		Dimension size = querySubpart.getSize();
		elementChild.putInteger( PluginTagNames.HEIGHT, size.height );
		elementChild.putInteger( PluginTagNames.WIDTH, size.width );
	}
	
	private void addLocation(QuerySubpart querySubpart, IMemento elementChild) {
		Point location = querySubpart.getLocation();
		elementChild.putInteger( PluginTagNames.XPOS, location.x );
		elementChild.putInteger( PluginTagNames.YPOS, location.y );
	}

	private void initializeTablesChild(Collection<IQueryTable> queryTables, IMemento memento) {
		IMemento tablesChild = memento.createChild(PluginTagNames.TABLES); 
		for ( IQueryTable queryTable : queryTables ) {
			IMemento queryTableChild = tablesChild.createChild(PluginTagNames.TABLE);
			initializeTableChild(queryTable, queryTableChild);
		}
	}
	
	private void initializeTableChild(IQueryTable queryTable, IMemento queryTableChild) {
		
		queryTableChild.putString( PluginTagNames.NAME, queryTable.getName() );
		queryTableChild.putString( PluginTagNames.DISPLAY_NAME, queryTable.getName() );
		queryTableChild.putString( PluginTagNames.CONNECTION_NAME, queryTable.getConnectionName() );
		queryTableChild.putString( PluginTagNames.CONNECTION_DISPLAY_NAME, queryTable.getConnectionDisplayName() );
		queryTableChild.putString( PluginTagNames.OBJECT_STORE_NAME, queryTable.getObjectStoreName() );
		queryTableChild.putString( PluginTagNames.OBJECT_STORE_DISPLAY_NAME, queryTable.getObjectStoreDisplayName() );
		queryTableChild.putBoolean( PluginTagNames.CBR_ENABLED, queryTable.isCBREnabled() );
		queryTableChild.putString( PluginTagNames.ALIAS, queryTable.getAlias() );
		
		IMemento fieldsChild = queryTableChild.createChild(PluginTagNames.FIELDS);
		initializeQueryFields(queryTable,fieldsChild);
		
		initializeTablesChild(queryTable.getChildQueryTables(), queryTableChild);
	}

	private void initializeQueryFields(IQueryTable queryTable, IMemento queryFieldsChild) {
		for ( IQueryField queryField : queryTable.getQueryFields() ) {
			IMemento queryFieldChild = queryFieldsChild.createChild(PluginTagNames.QUERY_FIELD );
			initializeQueryField(queryField,queryFieldChild);
		}
	}

	private void initializeQueryField(IQueryField queryField, IMemento queryFieldChild) {
		queryFieldChild.putString( PluginTagNames.NAME, queryField.getName() );
		queryFieldChild.putString( PluginTagNames.DISPLAY_NAME, queryField.getName() );
		queryFieldChild.putString( PluginTagNames.TYPE, queryField.getType().name() );
		queryFieldChild.putString( PluginTagNames.SORT_TYPE, queryField.getSortType().name() );
		queryFieldChild.putInteger( PluginTagNames.SORT_ORDER, queryField.getSortOrder() );
		queryFieldChild.putBoolean( PluginTagNames.SELECTED, queryField.isSelected() );
		queryFieldChild.putBoolean( PluginTagNames.ORDERABLE, queryField.isOrderable() );
		queryFieldChild.putBoolean( PluginTagNames.SUPPORTS_WILDCARDS, queryField.isSupportsWildcards() );
		queryFieldChild.putBoolean( PluginTagNames.CONTAINABLE, queryField.isContainable() );
		queryFieldChild.putBoolean( PluginTagNames.IS_QUERY_FIELD, queryField.isQueryField() );
		queryFieldChild.putBoolean( PluginTagNames.CBR_ENABLED, queryField.isCBREnabled() );
		queryFieldChild.putString( PluginTagNames.ALIAS, queryField.getAlias() );
	}

	public Query read() throws IOException, WorkbenchException {

		FileReader fileReader = new FileReader( file );
		XMLMemento memento = XMLMemento.createReadRoot( fileReader );
		return getQuery(memento);
	}

	private Query getQuery(XMLMemento memento) {
		Query query = new Query();
		
		query.setMaxCount( memento.getInteger( PluginTagNames.MAX_COUNT ) );
		query.setDistinct( memento.getBoolean( PluginTagNames.DISTINCT ) );
		query.setIncludeSubclasses( memento.getBoolean( PluginTagNames.INCLUDE_SUBCLASSES ) );
		query.setName( memento.getString( PluginTagNames.NAME ) );

		IMemento tablesChild = memento.getChild(PluginTagNames.TABLES); 
		for ( IMemento queryTableChild : tablesChild.getChildren(PluginTagNames.TABLE) ) {
			getQueryTable(query, queryTableChild);
		}

		IMemento diagramChild = memento.getChild(PluginTagNames.DIAGRAM);

		QueryDiagram queryDiagram = query.getQueryDiagram();
		getQueryDiagramChildren(diagramChild, queryDiagram, query);

		return query;
	}

	private void getQueryDiagramChildren(IMemento diagramChild, QueryDiagram queryDiagram, Query query) {
		
		IMemento diagramChildChildren = diagramChild.getChild(PluginTagNames.CHILDREN); 
		
		for (IMemento m : diagramChildChildren.getChildren(PluginTagNames.QUERY_CONTAINER) ) {
			QueryElement queryElement = getQueryContainerChild(m, query);
			queryDiagram.addChild(queryElement);
		}

		for (IMemento m : diagramChildChildren.getChildren(PluginTagNames.QUERY_COMPONENT) ) {
			QueryElement q = getQueryComponentChild(m, query);
			queryDiagram.addChild(q);
		}
	}

	private QueryElement getQueryComponentChild(IMemento m, Query query) {
		
		QueryComponentType type = QueryComponentType.valueOf( m.getString(PluginTagNames.TYPE) );
		QueryComponent queryComponent;
		switch ( type ) {

		case COMPARISON:
			Comparison comparison = new Comparison(query);
			ComparisonOperation comparisonOperation = ComparisonOperation.valueOf( m.getString(PluginTagNames.COMPARISON_OPERATION) );
			comparison.setComparisonOperation(comparisonOperation);
			comparison.setValue( getValue(m) );
			queryComponent = comparison;
			break;
			
		case FREE_TEXT:
			FreeText freeText = new FreeText(query);
			freeText.setText( m.getString(PluginTagNames.TEXT ) );
			queryComponent = freeText;
			break;

		case IN_FOLDER_TEST:
			InFolderTest inFolderTest = new InFolderTest(query);
			inFolderTest.setFolder( m.getString(PluginTagNames.FOLDER) );
			queryComponent = inFolderTest;
			break;
		
		case IN_SUBFOLDER_TEST:
			InSubFolderTest inSubFolderTest = new InSubFolderTest(query);
			inSubFolderTest.setFolder( m.getString(PluginTagNames.FOLDER) );
			queryComponent = inSubFolderTest;
			break;

		case CLASS_TEST:
			ClassTest classTest = new ClassTest(query);
			classTest.setClassName( m.getString(PluginTagNames.CLASS_NAME) );
			queryComponent = classTest;
			break;
		
		case NULL_TEST:
			NullTest nullTest = new NullTest(query);
			nullTest.setNegated( m.getBoolean(PluginTagNames.NEGATED) );
			queryComponent = nullTest;
			break;
			
		case WILDCARD_TEST:
			WildcardTest wildcardTest = new WildcardTest(query);
			WildcardType wildcardType = WildcardType.valueOf( m.getString(PluginTagNames.WILDCARD_TYPE) );
			wildcardTest.setWildcardType(wildcardType);
			wildcardTest.setValue( m.getString(PluginTagNames.STRING_VALUE) );
			queryComponent = wildcardTest;
			break;

		case FULL_TEXT:
			FullTextQuery fullTextQuery = new FullTextQuery(query);
			fullTextQuery.setAllFields( m.getBoolean(PluginTagNames.ALL_FIELDS ) );
			fullTextQuery.setText(m.getString(PluginTagNames.TEXT) );
			FullTextQueryType fullTextQueryType = FullTextQueryType.valueOf(m
					.getString(PluginTagNames.FULL_TEXT_QUERY_TYPE));
			fullTextQuery.setFullTextQueryType(fullTextQueryType);
			queryComponent = fullTextQuery;
			break;
		default:
			throw new IllegalArgumentException();
		}

		String fieldName = m.getString(PluginTagNames.FIELD_NAME );
		String tableName = m.getString(PluginTagNames.TABLE_NAME );
		IQueryField field = query.getField( fieldName, tableName );
		queryComponent.setField( field );
		
		getLocation(m, queryComponent );
		getSize(m, queryComponent );

		Boolean mainQuery = m.getBoolean(PluginTagNames.MAIN_QUERY );
		if ( mainQuery != null && mainQuery.booleanValue() ) {
			query.setMainQuery(queryComponent);
		}
		
		return queryComponent;
	}

	private Object getValue(IMemento m ) {
		
		Boolean booleanValue = m.getBoolean( PluginTagNames.BOOLEAN_VALUE );
		if ( booleanValue != null ) {
			return booleanValue;
		}
		
		String stringValue = m.getString(PluginTagNames.STRING_VALUE );
		if ( stringValue != null ) {
			return stringValue;
		}
		
		String dateStringValue = m.getString(PluginTagNames.DATE_VALUE);
		if ( dateStringValue != null ) {
			SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);
			try {
				return dateFormatter.parse(dateStringValue);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}
		
		String intStringValue = m.getString(PluginTagNames.INTEGER_VALUE );
		if ( intStringValue != null ) {
			return new Integer( intStringValue );
		}
		
		String doubleStringValue = m.getString(PluginTagNames.DOUBLE_VALUE );
		if ( doubleStringValue != null ) {
			return new Double(doubleStringValue);
		}

		throw new IllegalArgumentException();
	}
	
	private QueryContainer getQueryContainerChild(IMemento queryContainerChild, Query query ) {

		QueryContainerType containerType = QueryContainerType.valueOf( queryContainerChild.getString(PluginTagNames.TYPE ) );
		QueryContainer queryContainer;
		switch ( containerType ) {
		case AND_CONTAINER:
			queryContainer = new AndContainer( query );
			break;
		case NOT_CONTAINER:
			queryContainer = new NotContainer( query );
			break;
		case OR_CONTAINER:
			queryContainer = new OrContainer( query );
			break;
		default:
			throw new IllegalArgumentException();
		}
		
		getLocation(queryContainerChild, queryContainer );
		getSize(queryContainerChild, queryContainer);
		
		Boolean mainQuery = queryContainerChild.getBoolean(PluginTagNames.MAIN_QUERY );
		if ( mainQuery != null && mainQuery.booleanValue() ) {
			query.setMainQuery(queryContainer);
		}

		getQueryDiagramChildren(queryContainerChild, queryContainer, query);
		
		return queryContainer;
	}
	
	private void getSize(IMemento elementChild, QuerySubpart querySubpart) {
		Dimension size = new Dimension(elementChild.getInteger(PluginTagNames.WIDTH), elementChild
				.getInteger(PluginTagNames.HEIGHT));
		querySubpart.setSize(size);
	}

	private void getLocation(IMemento elementChild, QuerySubpart querySubpart) {
		Point location = new Point(elementChild.getInteger(PluginTagNames.XPOS), elementChild
				.getInteger(PluginTagNames.YPOS));
		querySubpart.setLocation(location);
	}
	
	private void getQueryTable(Query query, IMemento queryTableChild) {
		
		IQueryTable queryTable = getTable(queryTableChild);
		query.add(queryTable);
	}

	private IQueryTable getTable(IMemento queryTableChild) {

		String name = queryTableChild.getString( PluginTagNames.NAME );
		String displayName = queryTableChild.getString( PluginTagNames.DISPLAY_NAME );
		String connectionName = queryTableChild.getString( PluginTagNames.CONNECTION_NAME );
		String connectionDisplayName = queryTableChild.getString( PluginTagNames.CONNECTION_DISPLAY_NAME );
		String objectStoreName = queryTableChild.getString( PluginTagNames.OBJECT_STORE_NAME );
		String objectStoreDisplayName = queryTableChild.getString( PluginTagNames.OBJECT_STORE_DISPLAY_NAME );
		boolean cbrEnabled = queryTableChild.getBoolean(PluginTagNames.CBR_ENABLED );

		IQueryTable queryTable = new QueryTable2(name, displayName, objectStoreName, objectStoreDisplayName, connectionName, connectionDisplayName, cbrEnabled );
		
		IMemento queryFieldsChild = queryTableChild.getChild(PluginTagNames.FIELDS);
		for (IMemento queryFieldChild : queryFieldsChild.getChildren( PluginTagNames.QUERY_FIELD ) ) {
			IQueryField queryField = getQueryField(queryFieldChild, queryTable );  
			queryTable.addQueryField(queryField);
		}
		
		IMemento tablesChild = queryTableChild.getChild(PluginTagNames.TABLES); 
		for (IMemento childTableChild : tablesChild.getChildren(PluginTagNames.TABLE)) {
			IQueryTable childTable = getTable(childTableChild);
			queryTable.addChildQueryTable(childTable);
		}
		
		return queryTable;
	}

	private IQueryField getQueryField(IMemento queryFieldChild, IQueryTable queryTable) {
		
		String name = queryFieldChild.getString( PluginTagNames.NAME );
		IQueryField queryField;
		if ( "This".equals( name ) ) {
			queryField = new ThisQueryField(queryTable);
		} else {
			String displayName = queryFieldChild.getString( PluginTagNames.DISPLAY_NAME );
			QueryFieldType type = QueryFieldType.valueOf( queryFieldChild.getString( PluginTagNames.TYPE ) );
			
			Boolean orderable = queryFieldChild.getBoolean( PluginTagNames.ORDERABLE );
			Boolean containable = queryFieldChild.getBoolean( PluginTagNames.CONTAINABLE );
			boolean cbrEnabled = queryFieldChild.getBoolean(PluginTagNames.CBR_ENABLED );
			
			queryField = new QueryField2(name, displayName, type, orderable, containable, cbrEnabled, queryTable );
		}
		
		SortType sortType = SortType.valueOf( queryFieldChild.getString( PluginTagNames.SORT_TYPE ) );
		queryField.setSortType(sortType);
		queryField.setSortOrder( queryFieldChild.getInteger( PluginTagNames.SORT_ORDER ) );
		queryField.setSelected( queryFieldChild.getBoolean( PluginTagNames.SELECTED ) );
		
		return queryField;
		
	}
	
	public QueryFileInfo getInfo() throws WorkbenchException, FileNotFoundException {
		FileReader fileReader = new FileReader( file );
		XMLMemento memento = XMLMemento.createReadRoot( fileReader );

		String name = memento.getString( PluginTagNames.NAME );
		String sql = memento.getString( PluginTagNames.SQL );
		Date dateLastModified = new Date( file.lastModified() );
		
		return new QueryFileInfo(name,sql,dateLastModified);
	}
}
