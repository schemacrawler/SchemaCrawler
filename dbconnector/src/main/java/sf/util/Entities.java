/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package sf.util;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Escapes entities.
 * 
 * @author Sualeh Fatehi
 */
public class Entities
{

  private final static Map<Integer, String> BASIC_ENTITIES_MAP;
  private final static Map<Integer, String> APOS_ENTITIES_MAP;
  private final static Map<Integer, String> ISO8859_1_ENTITIES_MAP;
  private final static Map<Integer, String> HTML40_ENTITIES_MAP;

  static
  {
    Map<Integer, String> map = new HashMap<Integer, String>();

    map.clear();
    map.put(34, "quot");
    map.put(62, "gt");
    map.put(38, "amp");
    map.put(60, "lt");
    BASIC_ENTITIES_MAP = Collections.unmodifiableMap(map);

    map.clear();
    map.put(39, "apos");
    APOS_ENTITIES_MAP = Collections.unmodifiableMap(map);

    map.clear();
    map.put(190, "frac34");
    map.put(246, "ouml");
    map.put(235, "euml");
    map.put(214, "Ouml");
    map.put(207, "Iuml");
    map.put(171, "laquo");
    map.put(241, "ntilde");
    map.put(223, "szlig");
    map.put(251, "ucirc");
    map.put(240, "eth");
    map.put(213, "Otilde");
    map.put(198, "AElig");
    map.put(230, "aelig");
    map.put(187, "raquo");
    map.put(208, "ETH");
    map.put(176, "deg");
    map.put(225, "aacute");
    map.put(169, "copy");
    map.put(188, "frac14");
    map.put(252, "uuml");
    map.put(224, "agrave");
    map.put(160, "nbsp");
    map.put(201, "Eacute");
    map.put(189, "frac12");
    map.put(234, "ecirc");
    map.put(210, "Ograve");
    map.put(186, "ordm");
    map.put(250, "uacute");
    map.put(231, "ccedil");
    map.put(203, "Euml");
    map.put(175, "macr");
    map.put(209, "Ntilde");
    map.put(162, "cent");
    map.put(194, "Acirc");
    map.put(226, "acirc");
    map.put(245, "otilde");
    map.put(172, "not");
    map.put(181, "micro");
    map.put(191, "iquest");
    map.put(166, "brvbar");
    map.put(205, "Iacute");
    map.put(212, "Ocirc");
    map.put(229, "aring");
    map.put(165, "yen");
    map.put(192, "Agrave");
    map.put(248, "oslash");
    map.put(228, "auml");
    map.put(185, "sup1");
    map.put(247, "divide");
    map.put(249, "ugrave");
    map.put(254, "thorn");
    map.put(232, "egrave");
    map.put(227, "atilde");
    map.put(222, "THORN");
    map.put(215, "times");
    map.put(163, "pound");
    map.put(200, "Egrave");
    map.put(168, "uml");
    map.put(182, "para");
    map.put(177, "plusmn");
    map.put(199, "Ccedil");
    map.put(180, "acute");
    map.put(161, "iexcl");
    map.put(244, "ocirc");
    map.put(193, "Aacute");
    map.put(233, "eacute");
    map.put(243, "oacute");
    map.put(221, "Yacute");
    map.put(206, "Icirc");
    map.put(253, "yacute");
    map.put(216, "Oslash");
    map.put(211, "Oacute");
    map.put(179, "sup3");
    map.put(196, "Auml");
    map.put(170, "ordf");
    map.put(178, "sup2");
    map.put(242, "ograve");
    map.put(239, "iuml");
    map.put(195, "Atilde");
    map.put(183, "middot");
    map.put(218, "Uacute");
    map.put(255, "yuml");
    map.put(167, "sect");
    map.put(219, "Ucirc");
    map.put(236, "igrave");
    map.put(204, "Igrave");
    map.put(237, "iacute");
    map.put(173, "shy");
    map.put(197, "Aring");
    map.put(220, "Uuml");
    map.put(238, "icirc");
    map.put(164, "curren");
    map.put(184, "cedil");
    map.put(174, "reg");
    map.put(217, "Ugrave");
    map.put(202, "Ecirc");
    map.put(190, "frac34");
    map.put(246, "ouml");
    map.put(235, "euml");
    map.put(214, "Ouml");
    map.put(207, "Iuml");
    map.put(171, "laquo");
    map.put(241, "ntilde");
    map.put(223, "szlig");
    map.put(251, "ucirc");
    map.put(240, "eth");
    map.put(213, "Otilde");
    map.put(198, "AElig");
    map.put(230, "aelig");
    map.put(187, "raquo");
    map.put(208, "ETH");
    map.put(176, "deg");
    map.put(225, "aacute");
    map.put(169, "copy");
    map.put(188, "frac14");
    map.put(252, "uuml");
    map.put(224, "agrave");
    map.put(160, "nbsp");
    map.put(201, "Eacute");
    map.put(189, "frac12");
    map.put(234, "ecirc");
    map.put(210, "Ograve");
    map.put(186, "ordm");
    map.put(250, "uacute");
    map.put(231, "ccedil");
    map.put(203, "Euml");
    map.put(175, "macr");
    map.put(209, "Ntilde");
    map.put(162, "cent");
    map.put(194, "Acirc");
    map.put(226, "acirc");
    map.put(245, "otilde");
    map.put(172, "not");
    map.put(181, "micro");
    map.put(191, "iquest");
    map.put(166, "brvbar");
    map.put(205, "Iacute");
    map.put(212, "Ocirc");
    map.put(229, "aring");
    map.put(165, "yen");
    map.put(192, "Agrave");
    map.put(248, "oslash");
    map.put(228, "auml");
    map.put(185, "sup1");
    map.put(247, "divide");
    map.put(249, "ugrave");
    map.put(254, "thorn");
    map.put(232, "egrave");
    map.put(227, "atilde");
    map.put(222, "THORN");
    map.put(215, "times");
    map.put(163, "pound");
    map.put(200, "Egrave");
    map.put(168, "uml");
    map.put(182, "para");
    map.put(177, "plusmn");
    map.put(199, "Ccedil");
    map.put(180, "acute");
    map.put(161, "iexcl");
    map.put(244, "ocirc");
    map.put(193, "Aacute");
    map.put(233, "eacute");
    map.put(243, "oacute");
    map.put(221, "Yacute");
    map.put(206, "Icirc");
    map.put(253, "yacute");
    map.put(216, "Oslash");
    map.put(211, "Oacute");
    map.put(179, "sup3");
    map.put(196, "Auml");
    map.put(170, "ordf");
    map.put(178, "sup2");
    map.put(242, "ograve");
    map.put(239, "iuml");
    map.put(195, "Atilde");
    map.put(183, "middot");
    map.put(218, "Uacute");
    map.put(255, "yuml");
    map.put(167, "sect");
    map.put(219, "Ucirc");
    map.put(236, "igrave");
    map.put(204, "Igrave");
    map.put(237, "iacute");
    map.put(173, "shy");
    map.put(197, "Aring");
    map.put(220, "Uuml");
    map.put(238, "icirc");
    map.put(164, "curren");
    map.put(184, "cedil");
    map.put(174, "reg");
    map.put(217, "Ugrave");
    map.put(202, "Ecirc");
    ISO8859_1_ENTITIES_MAP = Collections.unmodifiableMap(map);

    map.clear();
    map.put(8773, "cong");
    map.put(982, "piv");
    map.put(9002, "rang");
    map.put(960, "pi");
    map.put(928, "Pi");
    map.put(8901, "sdot");
    map.put(8801, "equiv");
    map.put(8744, "or");
    map.put(927, "Omicron");
    map.put(9001, "lang");
    map.put(8776, "asymp");
    map.put(8745, "cap");
    map.put(958, "xi");
    map.put(8711, "nabla");
    map.put(8839, "supe");
    map.put(926, "Xi");
    map.put(8968, "lceil");
    map.put(8743, "and");
    map.put(8746, "cup");
    map.put(937, "Omega");
    map.put(913, "Alpha");
    map.put(352, "Scaron");
    map.put(8465, "image");
    map.put(8869, "perp");
    map.put(8969, "rceil");
    map.put(914, "Beta");
    map.put(8240, "permil");
    map.put(8805, "ge");
    map.put(946, "beta");
    map.put(8722, "minus");
    map.put(338, "OElig");
    map.put(8804, "le");
    map.put(8657, "uArr");
    map.put(959, "omicron");
    map.put(915, "Gamma");
    map.put(8656, "lArr");
    map.put(402, "fnof");
    map.put(8713, "notin");
    map.put(8658, "rArr");
    map.put(8230, "hellip");
    map.put(920, "Theta");
    map.put(925, "Nu");
    map.put(8712, "isin");
    map.put(376, "Yuml");
    map.put(949, "epsilon");
    map.put(8747, "int");
    map.put(962, "sigmaf");
    map.put(8596, "harr");
    map.put(918, "Zeta");
    map.put(950, "zeta");
    map.put(8660, "hArr");
    map.put(929, "Rho");
    map.put(963, "sigma");
    map.put(964, "tau");
    map.put(932, "Tau");
    map.put(8970, "lfloor");
    map.put(8212, "mdash");
    map.put(919, "Eta");
    map.put(8195, "emsp");
    map.put(921, "Iota");
    map.put(8211, "ndash");
    map.put(8243, "Prime");
    map.put(8595, "darr");
    map.put(8727, "lowast");
    map.put(8659, "dArr");
    map.put(969, "omega");
    map.put(8242, "prime");
    map.put(8709, "empty");
    map.put(923, "Lambda");
    map.put(967, "chi");
    map.put(961, "rho");
    map.put(931, "Sigma");
    map.put(954, "kappa");
    map.put(8592, "larr");
    map.put(8707, "exist");
    map.put(8194, "ensp");
    map.put(968, "psi");
    map.put(922, "Kappa");
    map.put(9830, "diams");
    map.put(936, "Psi");
    map.put(965, "upsilon");
    map.put(8835, "sup");
    map.put(8704, "forall");
    map.put(9829, "hearts");
    map.put(8736, "ang");
    map.put(957, "nu");
    map.put(8756, "there4");
    map.put(8834, "sub");
    map.put(917, "Epsilon");
    map.put(935, "Chi");
    map.put(955, "lambda");
    map.put(8706, "part");
    map.put(8221, "rdquo");
    map.put(8734, "infin");
    map.put(8364, "euro");
    map.put(8254, "oline");
    map.put(8249, "lsaquo");
    map.put(8222, "bdquo");
    map.put(953, "iota");
    map.put(710, "circ");
    map.put(8250, "rsaquo");
    map.put(353, "scaron");
    map.put(966, "phi");
    map.put(924, "Mu");
    map.put(8224, "dagger");
    map.put(8204, "zwnj");
    map.put(8501, "alefsym");
    map.put(978, "upsih");
    map.put(8216, "lsquo");
    map.put(8593, "uarr");
    map.put(9674, "loz");
    map.put(8715, "ni");
    map.put(8629, "crarr");
    map.put(8971, "rfloor");
    map.put(8764, "sim");
    map.put(933, "Upsilon");
    map.put(8594, "rarr");
    map.put(8800, "ne");
    map.put(8476, "real");
    map.put(8201, "thinsp");
    map.put(956, "mu");
    map.put(8220, "ldquo");
    map.put(8225, "Dagger");
    map.put(934, "Phi");
    map.put(8733, "prop");
    map.put(9827, "clubs");
    map.put(9824, "spades");
    map.put(8260, "frasl");
    map.put(8206, "lrm");
    map.put(8853, "oplus");
    map.put(8719, "prod");
    map.put(947, "gamma");
    map.put(8205, "zwj");
    map.put(8482, "trade");
    map.put(339, "oelig");
    map.put(916, "Delta");
    map.put(8226, "bull");
    map.put(952, "theta");
    map.put(8472, "weierp");
    map.put(8838, "sube");
    map.put(945, "alpha");
    map.put(8855, "otimes");
    map.put(977, "thetasym");
    map.put(8730, "radic");
    map.put(8207, "rlm");
    map.put(8721, "sum");
    map.put(951, "eta");
    map.put(8218, "sbquo");
    map.put(8217, "rsquo");
    map.put(732, "tilde");
    map.put(948, "delta");
    HTML40_ENTITIES_MAP = Collections.unmodifiableMap(map);
  }

  /** XML character entities. */
  public final static Entities XML = new Entities(BASIC_ENTITIES_MAP,
                                                  APOS_ENTITIES_MAP);
  /** HTML 3.2 character entities. */
  public final static Entities HTML32 = new Entities(BASIC_ENTITIES_MAP,
                                                     ISO8859_1_ENTITIES_MAP);
  /** HTML 4.0 character entities. */
  public final static Entities HTML40 = new Entities(BASIC_ENTITIES_MAP,
                                                     ISO8859_1_ENTITIES_MAP,
                                                     HTML40_ENTITIES_MAP);

  private final Map<Integer, String> charEntityMap;
  private final Map<String, Integer> entityCharMap;

  private Entities(Map<Integer, String>... maps)
  {
    Map<Integer, String> workingCharEntityMap = new HashMap<Integer, String>();
    for (Map<Integer, String> map: maps)
    {
      workingCharEntityMap.putAll(map);
    }
    charEntityMap = Collections.unmodifiableMap(workingCharEntityMap);
    entityCharMap = flipMap();
  }

  private Map<String, Integer> flipMap()
  {
    Map<String, Integer> workingEntityCharMap = new HashMap<String, Integer>();
    for (Map.Entry<Integer, String> charEntity: charEntityMap.entrySet())
    {
      workingEntityCharMap.put(charEntity.getValue(), charEntity.getKey());
    }
    return Collections.unmodifiableMap(workingEntityCharMap);
  }

  /**
   * <p>
   * Escapes the characters in a <code>String</code>.
   * 
   * @param str
   *        The <code>String</code> to escape.
   * @return A new escaped <code>String</code>.
   */
  public String escape(final String str)
  {
    // todo: rewrite to use a Writer
    final StringBuffer buf = new StringBuffer(str.length() * 2);
    int i;
    for (i = 0; i < str.length(); ++i)
    {
      final char ch = str.charAt(i);
      final String entityName = charEntityMap.get(ch);
      if (entityName == null)
      {
        if (ch > 0x7F)
        {
          final int intValue = ch;
          buf.append("&#");
          buf.append(intValue);
          buf.append(';');
        }
        else
        {
          buf.append(ch);
        }
      }
      else
      {
        buf.append('&');
        buf.append(entityName);
        buf.append(';');
      }
    }
    return buf.toString();
  }

  /**
   * Unescapes the entities in a <code>String</code>.
   * 
   * @param str
   *        The <code>String</code> to escape.
   * @return A new escaped <code>String</code>.
   */
  public String unescape(final String str)
  {
    final StringBuffer buf = new StringBuffer(str.length());
    int i;
    for (i = 0; i < str.length(); ++i)
    {
      final char ch = str.charAt(i);
      if (ch == '&')
      {
        final int semi = str.indexOf(';', i + 1);
        if (semi == -1)
        {
          buf.append(ch);
          continue;
        }
        final String entityName = str.substring(i + 1, semi);
        int entityValue;
        if (entityName.charAt(0) == '#')
        {
          final char charAt1 = entityName.charAt(1);
          if (charAt1 == 'x' || charAt1 == 'X')
          {
            entityValue = Integer.valueOf(entityName.substring(2), 16)
              .intValue();
          }
          else
          {
            entityValue = Integer.parseInt(entityName.substring(1));
          }
        }
        else
        {
          entityValue = entityCharMap.get(entityName);
        }
        if (entityValue == -1)
        {
          buf.append('&');
          buf.append(entityName);
          buf.append(';');
        }
        else
        {
          buf.append(entityValue);
        }
        i = semi;
      }
      else
      {
        buf.append(ch);
      }
    }
    return buf.toString();
  }

}
