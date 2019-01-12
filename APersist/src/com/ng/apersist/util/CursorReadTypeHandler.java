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
package com.ng.apersist.util;


import android.database.Cursor;

public class CursorReadTypeHandler {

  public Object createObjectFromCursor(Cursor c, int column ) {
    int type = c.getType( column );
    return readValueFromCursorWithType( type, c, column );
  }

  private Object readValueFromCursorWithType( int type, Cursor c, int column ) {
    switch ( type ) {
      case Cursor.FIELD_TYPE_INTEGER:
        return c.getInt( column );
      case Cursor.FIELD_TYPE_FLOAT:
        return c.getFloat( column );
      case Cursor.FIELD_TYPE_STRING:
        return c.getString( column );
      default:
        return null;
    }
  }

}