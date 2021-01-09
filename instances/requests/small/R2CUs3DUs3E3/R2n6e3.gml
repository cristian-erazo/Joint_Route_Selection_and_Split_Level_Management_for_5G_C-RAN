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
    type 2
    prc 2
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 3
    prb 3
    x 69
    y 48
  ]
  node [
    id 4
    label "4"
    type 1
    prc 3
    ant 1
    prb 2
    x 92
    y 65
  ]
  node [
    id 5
    label "5"
    type 1
    prc 1
    ant 1
    prb 4
    x 29
    y 82
  ]
  edge [
    source 0
    target 4
    bandwith 829
    delay 257
  ]
  edge [
    source 1
    target 5
    bandwith 282
    delay 276
  ]
  edge [
    source 2
    target 3
    bandwith 643
    delay 390
  ]
]
