/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.command.chatgpt.embeddings;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.linear.ArrayRealVector;
import static java.util.Objects.requireNonNull;

public class ListRealVector extends ArrayRealVector {

  private static final Logger LOGGER = Logger.getLogger(ListRealVector.class.getCanonicalName());

  private static final long serialVersionUID = -7262722041120229630L;

  private static double[] convertListToArray(final List<Double> list) {
    requireNonNull(list, "No embedding provided");

    final int size = list.size();
    final double[] array = new double[size];

    for (int i = 0; i < size; i++) {
      final Double value = list.get(i);
      if (value == null) {
        LOGGER.log(Level.WARNING, "Embedding contains null values");
        continue;
      }
      array[i] = value;
    }

    return array;
  }

  /** The embedding vector */
  private final List<Double> embedding;

  public ListRealVector() {
    embedding = new ArrayList<>();
  }

  public ListRealVector(final List<Double> embedding) {
    super(convertListToArray(embedding));
    this.embedding = embedding;
  }

  public List<Double> getEmbedding() {
    return embedding;
  }
}
