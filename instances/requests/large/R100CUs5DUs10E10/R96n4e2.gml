graph [
  node [
    id 0
    label "0"
    type 2
    prc 4
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
    prc 5
    ant 2
    prb 3
    x 26
    y 38
  ]
  node [
    id 3
    label "3"
    type 1
    prc 1
    ant 6
    prb 2
    x 58
    y 21
  ]
  edge [
    source 0
    target 2
    bandwith 945
    delay 125
  ]
  edge [
    source 1
    target 3
    bandwith 662
    delay 389
  ]
]
