# Copyright (c) Sualeh Fatehi
# SPDX-License-Identifier: EPL-2.0

def strip_name(entity):
    """Clean up names since Mermaid only allows alphanumeric identifiers"""
    cleanedname = support.stripName(entity)
    if not cleanedname:
        cleanedname = "UNKNOWN"
    return cleanedname

def label_for(relationship):
    """Generate relationship label indicating bridge tables."""
    cardinality = relationship.getType()
    left_entity = relationship.getLeftEntity()
    right_entity = relationship.getRightEntity()
    if cardinality.name() == 'many_many':
        return f'via bridge table {relationship.getName()}'
    elif left_entity == right_entity:
        return "self-reference"
    elif relationship.hasRemarks():
        remarks = support.remarks(relationship)
        return remarks
    else:
        return 'foreign key'

# -----------


print('---')
if title:
  print(f'title: "{title}"')
print('config:')
print('  theme: base')
print('---')
  
print('erDiagram')
print('')
print('  classDef strong_entity stroke:#283593;')
print('  classDef subtype stroke:#1976D2;')
print('  classDef weak_entity stroke:#1976D2;')
print('  classDef unknown stroke:#AAAAAA;')
print('  classDef non_entity stroke:#AAAAAA;')
print('')

for entity in support.entities():
    print(f'  "{support.cleanFullName(entity)}":::{entity.getType()} {{')
    for entity_attribute in entity.getEntityAttributes():
        attribute_type = entity_attribute.getType()
        print(f'    {attribute_type} {strip_name(entity_attribute)}', end='')
        if entity_attribute.hasRemarks():
            remarks = support.remarks(entity_attribute)
            print(f' "{remarks}"', end='')
        print()
    print('  }')
    print('')

for relationship in er_model.getRelationships():
    left_entity = relationship.getLeftEntity()
    right_entity = relationship.getRightEntity()
    cardinality = relationship.getType()
    cardinality_symbol = support.cardinalitySymbol(relationship)
    label = label_for(relationship)
    print(f'  "{support.cleanFullName(left_entity)}" {cardinality_symbol} "{support.cleanFullName(right_entity)}" : "{label}"')
