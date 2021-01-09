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
    prc 4
  ]
  node [
    id 2
    label "2"
    type 1
    prc 2
    ant 4
    prb 4
    x 57
    y 77
  ]
  node [
    id 3
    label "3"
    type 1
    prc 5
    ant 6
    prb 2
    x 89
    y 24
  ]
  node [
    id 4
    label "4"
    type 1
    prc 3
    ant 3
    prb 6
    x 56
    y 63
  ]
  edge [
    source 0
    target 3
    bandwith 764
    delay 427
  ]
  edge [
    source 1
    target 4
    bandwith 891
    delay 458
  ]
  edge [
    source 1
    target 2
    bandwith 734
    delay 148
  ]
]
