graph [
  node [
    id 0
    label "0"
    type 2
    prc 2
  ]
  node [
    id 1
    label "1"
    type 2
    prc 3
  ]
  node [
    id 2
    label "2"
    type 1
    prc 1
    ant 7
    prb 2
    x 42
    y 28
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 2
    prb 3
    x 85
    y 116
  ]
  node [
    id 4
    label "4"
    type 1
    prc 5
    ant 5
    prb 4
    x 70
    y 100
  ]
  node [
    id 5
    label "5"
    type 1
    prc 3
    ant 7
    prb 3
    x 52
    y 117
  ]
  edge [
    source 0
    target 2
    bandwith 634
    delay 222
  ]
  edge [
    source 0
    target 3
    bandwith 737
    delay 150
  ]
  edge [
    source 1
    target 4
    bandwith 942
    delay 287
  ]
  edge [
    source 1
    target 5
    bandwith 203
    delay 442
  ]
]
