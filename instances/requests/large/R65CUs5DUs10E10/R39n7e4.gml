graph [
  node [
    id 0
    label "0"
    type 2
    prc 3
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
    type 2
    prc 5
  ]
  node [
    id 3
    label "3"
    type 1
    prc 3
    ant 4
    prb 4
    x 66
    y 32
  ]
  node [
    id 4
    label "4"
    type 1
    prc 1
    ant 6
    prb 2
    x 15
    y 58
  ]
  node [
    id 5
    label "5"
    type 1
    prc 4
    ant 3
    prb 2
    x 76
    y 39
  ]
  node [
    id 6
    label "6"
    type 1
    prc 4
    ant 9
    prb 3
    x 61
    y 90
  ]
  edge [
    source 0
    target 5
    bandwith 472
    delay 205
  ]
  edge [
    source 1
    target 4
    bandwith 116
    delay 418
  ]
  edge [
    source 2
    target 3
    bandwith 595
    delay 306
  ]
  edge [
    source 2
    target 6
    bandwith 194
    delay 459
  ]
]
