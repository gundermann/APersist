/*   $HeadURL$
 * ----------------------------------------------------------------------------
 *     (c) by data experts gmbh
 *            Woldegker Str. 12
 *            17033 Neubrandenburg
 * ----------------------------------------------------------------------------
 *     Dieses Dokument und die hierin enthaltenen Informationen unterliegen
 *     dem Urheberrecht und duerfen ohne die schriftliche Genehmigung des
 *     Herausgebers weder als ganzes noch in Teilen dupliziert, reproduziert
 *     oder manipuliert werden.
 * ----------------------------------------------------------------------------
 *     $Id$
 * ----------------------------------------------------------------------------
 */
package com.ng.apersist.query;

import java.util.ArrayList;
import java.util.Collection;

import com.ng.apersist.annotation.AnnotationInterpreter;
import com.ng.apersist.dao.DAO;
import com.ng.apersist.dao.DaoManager;
import com.ng.apersist.dao.HelperDao;
import com.ng.apersist.dao.HelperDaoManager;
import com.ng.apersist.util.CursorReadTypeHandler;
import com.ng.apersist.util.NoPersistenceClassException;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class NativeQuery {

  private final String nativeQuery;

  private final SQLiteDatabase db;

  private final CursorReadTypeHandler typeHandler;

  public NativeQuery( final String nativeQuery, final SQLiteDatabase db ) {
    this.nativeQuery = nativeQuery;
    this.db = db;
    this.typeHandler = new CursorReadTypeHandler();
  }

  public Collection<Object[]> getResult() {
    Collection<Object[]> result = new ArrayList<>();
    Cursor c = db.rawQuery( nativeQuery, null );
    if ( c.getCount() == 0 ) {
      return result;
    }

    c.moveToFirst();
    do {
      Object[] o = new Object[c.getColumnCount()];
      for ( int i = 0; i < o.length; i++ ) {
        o[i] = typeHandler.createObjectFromCursor( c, i );
      }
      result.add( o );
    } while ( c.moveToNext() );
    return result;
  }

  @SuppressWarnings( "unchecked" )
  public <T> Collection<T> getResult( Class<T> returnClass ) {
    Cursor c = db.rawQuery( nativeQuery, null );
    if ( c.getCount() == 0 ) {
      return new ArrayList<>();
    }
    DAO<?> dao = DaoManager.getInstance().getDaoForType(returnClass);
    return (Collection<T>) dao.loadFromCursor( c );
  }

}