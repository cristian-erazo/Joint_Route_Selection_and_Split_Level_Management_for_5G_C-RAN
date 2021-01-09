graph [
  node [
    id 0
    label "0"
    type 2
    prc 5
  ]
  node [
    id 1
    label "1"
    type 2
    prc 5
  ]
  node [
    id 2
    label "2"
    type 2
    prc 4
  ]
  node [
    id 3
    label "3"
    type 2
    prc 5
  ]
  node [
    id 4
    label "4"
    type 1
    prc 5
    ant 2
    prb 6
    x 37
    y 81
  ]
  node [
    id 5
    label "5"
    type 1
    prc 1
    ant 4
    prb 6
    x 101
    y 90
  ]
  node [
    id 6
    label "6"
    type 1
    prc 2
    ant 8
    prb 4
    x 62
    y 102
  ]
  node [
    id 7
    label "7"
    type 1
    prc 5
    ant 7
    prb 6
    x 48
    y 15
  ]
  edge [
    source 0
    target 7
    bandwith 617
    delay 106
  ]
  edge [
    source 1
    target 6
    bandwith 903
    delay 445
  ]
  edge [
    source 2
    target 4
    bandwith 846
    delay 126
  ]
  edge [
    source 3
    target 5
    bandwith 899
    delay 475
  ]
]
