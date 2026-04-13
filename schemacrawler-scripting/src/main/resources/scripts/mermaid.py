# Copyright (c) Sualeh Fatehi
# SPDX-License-Identifier: EPL-2.0

import re
import java


def clean_name(name):
    """Clean up names since Mermaid only allows alphanumeric identifiers"""
    namepattern = r'[^-\d\w]'
    cleanedname = re.sub(namepattern, '', name)
    if not cleanedname:
        cleanedname = "UNKNOWN"
    return cleanedname

def symbol_for(cardinality):
    """Map `RelationshipCardinality` enum to Mermaid notation."""
    card_map = {
        'unknown':     '||--||',    # Default to 1:1 or adjust
        'zero_one':    '||--o|',
        'zero_many':   '||--o{',
        'one_one':     '||--||',
        'one_many':    '||--|{',
        'many_many':   '}o--o{'  # (bridge table implied)
    }
    return card_map.get(cardinality.name(), '||--o{')

def name_for(entity):
    entity_name = entity.getFullName().replace('"', '')
    entity_type = entity.getType().description()
    return '"' + entity_name + ' [' + entity_type + ']"'

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
        remarks = ' '.join(relationship.getRemarks().splitlines())
        return remarks
    else:
        return 'foreign key'

# -----------

print('erDiagram')
print('')

for entity in er_model.getEntities():
    print(f'  {name_for(entity)} {{')
    for entity_attribute in entity.getEntityAttributes():
        entity_attribute_name = entity_attribute.getName()
        attribute_type = entity_attribute.getType().toString()
        attribute_has_remarks = entity_attribute.hasRemarks()
        print(f'    {attribute_type} {clean_name(entity_attribute_name)}', end='')
        if attribute_has_remarks:
            remarks = ' '.join(entity_attribute.getRemarks().splitlines())
            print(f' "{remarks}"', end='')
        print()
    print('  }')
    print('')

for entity in er_model.getEntities():
    for relationship in entity.getRelationships():
        left_entity = relationship.getLeftEntity()
        right_entity = relationship.getRightEntity()
        if entity != left_entity:
            continue
        cardinality = relationship.getType()
        cardinality_symbol = symbol_for(cardinality)
        label = label_for(relationship)
        print(f'  {name_for(left_entity)} {cardinality_symbol} {name_for(right_entity)} : "{label}"')
