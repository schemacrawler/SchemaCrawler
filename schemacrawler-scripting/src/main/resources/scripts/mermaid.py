# Copyright (c) Sualeh Fatehi
# SPDX-License-Identifier: EPL-2.0

def strip_name(entity):
    """Clean up names since Mermaid only allows alphanumeric identifiers"""
    cleanedname = support.stripName(entity)
    if not cleanedname:
        cleanedname = "UNKNOWN"
    return cleanedname

def name_for(entity):
    entity_name = support.cleanFullName(entity)
    entity_type = entity.getType().description()
    return f'"{entity_name} [{entity_type}]"'

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

print('erDiagram')
print('')

for entity in er_model.getEntities():
    print(f'  {name_for(entity)} {{')
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
    print(f'  {name_for(left_entity)} {cardinality_symbol} {name_for(right_entity)} : "{label}"')
