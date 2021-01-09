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
    type 1
    prc 4
    ant 4
    prb 5
    x 90
    y 115
  ]
  node [
    id 2
    label "2"
    type 1
    prc 2
    ant 9
    prb 5
    x 53
    y 49
  ]
  node [
    id 3
    label "3"
    type 1
    prc 3
    ant 10
    prb 4
    x 15
    y 75
  ]
  edge [
    source 0
    target 2
    bandwith 672
    delay 369
  ]
  edge [
    source 0
    target 3
    bandwith 936
    delay 133
  ]
  edge [
    source 0
    target 1
    bandwith 767
    delay 473
  ]
]
